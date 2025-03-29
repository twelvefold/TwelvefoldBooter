package twelvefold.twelvefoldbooter;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

@IFMLLoadingPlugin.Name("TwelvefoldBooter")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(990)
public class TwelvefoldPlugin implements IFMLLoadingPlugin {

	public static final Logger LOGGER = LogManager.getLogger("TwelvefoldBooter");

	public TwelvefoldPlugin() {
		TwelvefoldEarlyConfig.init((File) FMLInjectionData.data()[6]);
		MixinBootstrap.init();
		MixinExtrasBootstrap.init();
		Mixins.addConfiguration("mixins.twelvefold.init.json");
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[]{TwelvefoldTransformer.class.getName()};
	}
	
	@Override
	public String getModContainerClass()
	{
		return TwelvefoldBooter.class.getName();
	}
	
	@Override
	public String getSetupClass()
	{
		return null;
	}

	/**
	 * Handle actually parsing and adding the early configurations here, as it gets called after all other plugins are initialized
	 */
	@Override
	public void injectData(Map<String, Object> data) {
		for(Map.Entry<String, List<Supplier<Boolean>>> entry : TwelvefoldRegistryAPI.getEarlyMixins().entrySet()) {
			//Check for removals
			if(TwelvefoldRegistryAPI.getRejectMixins().contains(entry.getKey())) {
				LOGGER.warn("TwelvefoldBooter received removal of \"" + entry.getKey() + "\" for early mixin application, rejecting.");
				continue;
			}
			//Check for enabled
			Boolean enabled = null;
			for(Supplier<Boolean> supplier : entry.getValue()) {
				if(Boolean.TRUE.equals(enabled)) continue;//Short circuit OR
				Boolean supplied = supplier.get();
				if(supplied == null) LOGGER.warn("TwelvefoldBooter received null value for individual supplier from \"" + entry.getKey() + "\" for early mixin application.");
				else enabled = supplied;
			}
			if(enabled == null) {
				LOGGER.warn("TwelvefoldBooter received null value for suppliers from \"" + entry.getKey() + "\" for early mixin application, ignoring.");
				continue;
			}
			//Add configuration
			if(enabled) {
				LOGGER.info("TwelvefoldBooter adding \"" + entry.getKey() + "\" for early mixin application.");
				Mixins.addConfiguration(entry.getKey());
			}
		}
	}
	
	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}