package twelvefold.twelvefoldbooter.mixin;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ModDiscoverer;
import org.spongepowered.asm.mixin.*;
import twelvefold.twelvefoldbooter.coremod.TwelvefoldPlugin;
import twelvefold.twelvefoldbooter.api.TwelvefoldRegistryAPI;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModClassLoader;
import net.minecraftforge.fml.common.ModContainer;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;
import twelvefold.twelvefoldbooter.api.LateMixinLoader;
import twelvefold.twelvefoldbooter.misc.TwelvefoldMisc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Loads non-vanilla and non-coremod mixins late in order to prevent ClassNotFound exceptions
 * Code based on original MIT Licensed code:
 * https://github.com/DimensionalDevelopment/JustEnoughIDs/blob/master/src/main/java/org/dimdev/jeid/mixin/init/JEIDMixinLoader.java
 */
@Mixin(Loader.class)
public class MixinLoader {

    @Shadow(remap = false)
    private List<ModContainer> mods;

    @Shadow(remap = false)
    private ModClassLoader modClassLoader;
    @Shadow(remap = false)
    private ModDiscoverer discoverer;
    /**
     * @reason Load all mods now and load mod support mixin configs. This can't be done later
     * since constructing mods loads classes from them.
     */
    @Inject(method = "loadMods", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/LoadController;transition(Lnet/minecraftforge/fml/common/LoaderState;Z)V", ordinal = 1), remap = false)
    private void beforeConstructingMods(List<String> nonMod, CallbackInfo ci) {
        for(ModContainer mod : mods) {
            try {
                modClassLoader.addFile(mod.getSource());
            }
            catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        //Start TwelvefoldBooter section
        ASMDataTable asmDataTable=discoverer.getASMTable();

        Set<ASMDataTable.ASMData> annotatedData = asmDataTable.getAll(LateMixinLoader.class.getName());
        if (!annotatedData.isEmpty()) {
            for (ASMDataTable.ASMData annotated : annotatedData) {
                try {
                    Class<?> clazz = Class.forName(annotated.getClassName());
                    TwelvefoldPlugin.LOGGER.info("Loading annotated late loader [{}] for its mixins.", clazz.getName());
                    if(clazz.isAnnotationPresent(LateMixinLoader.class))
                    {
                        LateMixinLoader lateMixinLoader=clazz.getAnnotation(LateMixinLoader.class);
                        if(lateMixinLoader.value() != null)
                        {
                            Predicate<String> shouldMixinConfigQueue = TwelvefoldMisc.getStringPredicate(lateMixinLoader, clazz);
                            TwelvefoldRegistryAPI.enqueueLateMixin(shouldMixinConfigQueue,lateMixinLoader.value());
                        }
                    }
                } catch (Throwable t) {
                    throw new RuntimeException("Unexpected error.", t);
                }
            }
        }

        for(Map.Entry<String, List<Supplier<Boolean>>> entry : TwelvefoldRegistryAPI.getLateMixins().entrySet()) {
            //Check for removals
            if(TwelvefoldRegistryAPI.getRejectMixins().contains(entry.getKey())) {
                TwelvefoldPlugin.LOGGER.warn("TwelvefoldBooter received removal of \"" + entry.getKey() + "\" for late mixin application, rejecting.");
                continue;
            }
            //Check for enabled
            Boolean enabled = null;
            for(Supplier<Boolean> supplier : entry.getValue()) {
                if(Boolean.TRUE.equals(enabled)) continue;//Short circuit OR
                Boolean supplied = supplier.get();
                if(supplied == null) TwelvefoldPlugin.LOGGER.warn("TwelvefoldBooter received null value for individual supplier from \"" + entry.getKey() + "\" for late mixin application.");
                else enabled = supplied;
            }
            if(enabled == null) {
                TwelvefoldPlugin.LOGGER.warn("TwelvefoldBooter received null value for supplier from \"" + entry.getKey() + "\" for late mixin application, ignoring.");
                continue;
            }
            //Add configuration
            if(enabled) {
                TwelvefoldPlugin.LOGGER.info("TwelvefoldBooter adding \"" + entry.getKey() + "\" for late mixin application.");
                Mixins.addConfiguration(entry.getKey());
            }
        }

        //Force clear the maps
        TwelvefoldRegistryAPI.clear();

        //End TwelvefoldBooter section

        try {
            Class<?> proxyClass = Class.forName("org.spongepowered.asm.mixin.transformer.Proxy");
            Field transformerField = proxyClass.getDeclaredField("transformer");
            transformerField.setAccessible(true);
            Object transformer = transformerField.get(null);

            Class<?> mixinTransformerClass = Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer");
            Field processorField = mixinTransformerClass.getDeclaredField("processor");
            processorField.setAccessible(true);
            Object processor = processorField.get(transformer);

            Class<?> mixinProcessorClass = Class.forName("org.spongepowered.asm.mixin.transformer.MixinProcessor");

            Field extensionsField = mixinProcessorClass.getDeclaredField("extensions");
            extensionsField.setAccessible(true);
            Object extensions = extensionsField.get(processor);

            Method selectConfigsMethod = mixinProcessorClass.getDeclaredMethod("selectConfigs", MixinEnvironment.class);
            selectConfigsMethod.setAccessible(true);
            selectConfigsMethod.invoke(processor, MixinEnvironment.getCurrentEnvironment());

            // Mixin 0.8.4+
            try {
                Method prepareConfigs = mixinProcessorClass.getDeclaredMethod("prepareConfigs", MixinEnvironment.class, Extensions.class);
                prepareConfigs.setAccessible(true);
                prepareConfigs.invoke(processor, MixinEnvironment.getCurrentEnvironment(), extensions);
                return;
            }
            catch (NoSuchMethodException ex) {
                // no-op
            }

            // Mixin 0.8+
            try {
                Method prepareConfigs = mixinProcessorClass.getDeclaredMethod("prepareConfigs", MixinEnvironment.class);
                prepareConfigs.setAccessible(true);
                prepareConfigs.invoke(processor, MixinEnvironment.getCurrentEnvironment());
                return;
            }
            catch (NoSuchMethodException ex) {
                // no-op
            }

            throw new UnsupportedOperationException("Unsupported Mixin");
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}