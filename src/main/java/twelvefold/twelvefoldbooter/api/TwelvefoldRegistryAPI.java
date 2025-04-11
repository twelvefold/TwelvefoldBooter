package twelvefold.twelvefoldbooter.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Enqueue mixins to be applied or rejected from your IFMLLoadingPlugin class init
 * Includes options for disabling the mixin from a Supplier, and loading it either early or late
 * Configuration name is the name of the json pointing to your mixin, such as "mixins.twelvefoldbooter.init.json"
 */
public abstract class TwelvefoldRegistryAPI {

    private static final Logger LOGGER = LogManager.getLogger("TwelvefoldRegistryAPI");

    private static HashMap<String, List<Supplier<Boolean>>> earlyMixins = new HashMap<>();
    private static HashMap<String, List<Supplier<Boolean>>> lateMixins = new HashMap<>();
    private static List<String> rejectMixins = new ArrayList<>();

    public static List<String> loadedClasses=new ArrayList<>();
    public static void enqueueEarlyMixin(Predicate<String> shouldMixinConfigQueue, String... configurations) {
        for (String configuration : configurations) {
            enqueueEarlyMixin(configuration, () -> shouldMixinConfigQueue.test(configuration));
        }
    }

    @SuppressWarnings("unused")
    public static void enqueueLateMixin(Predicate<String> shouldMixinConfigQueue, String... configurations) {
        for (String configuration : configurations) {
            enqueueLateMixin(configuration, () -> shouldMixinConfigQueue.test(configuration));
        }
    }

    /**
     * Register multiple mixin config resources at once to be applied
     *
     * @param configurations - mixin config resource names
     */
    @SuppressWarnings("unused")
    public static void enqueueEarlyMixin(String... configurations) {
        enqueueEarlyMixin(x -> true, configurations);
    }

    @Deprecated()
    public static void enqueueLateMixin(String... configurations) {
        enqueueLateMixin(x -> true, configurations);
    }

    /**
     * Add a mixin config resource to be applied, with a supplier to toggle application to be evaluated after all like-timed configs are registered
     * Note: If multiple suppliers are given for a single configuration, it is evaluated as OR
     *
     * @param configuration - mixin config resource name
     * @param supplier      - supplier to determine whether to apply the mixin or not
     */
    private static void ensureParameters(String configuration, Supplier<Boolean> supplier) {
        if (configuration == null || configuration.trim().isEmpty()) {
            throw new IllegalArgumentException("TwelvefoldRegistryAPI supplied null or empty configuration name during mixin enqueue, ignoring.");
        }
        if (supplier == null) {//Do not evaluate supplier.get() itself for null now
            throw new IllegalArgumentException("TwelvefoldRegistryAPI supplied null supplier for configuration \"" + configuration + "\" during mixin enqueue, ignoring.");
        }
    }

    public static void enqueueEarlyMixin(String configuration, Supplier<Boolean> supplier) {
        ensureParameters(configuration, supplier);
        //Process rejects prior to application
        LOGGER.info("TwelvefoldRegistryAPI supplied \"" + configuration + "\" for early mixin enqueue, adding.");
        earlyMixins.computeIfAbsent(configuration, k -> new ArrayList<>());
        earlyMixins.get(configuration).add(supplier);

    }

    public static void enqueueLateMixin(String configuration, Supplier<Boolean> supplier) {
        ensureParameters(configuration, supplier);
        LOGGER.info("TwelvefoldRegistryAPI supplied \"" + configuration + "\" for late mixin enqueue, adding.");
        lateMixins.computeIfAbsent(configuration, k -> new ArrayList<>());
        lateMixins.get(configuration).add(supplier);
    }

    /**
     * Designates a mixin config resource name to be ignored before application (Will only affect TwelvefoldBooter applied mixins)
     * Note: Realistically you should not use this, but it is provided in the case of specific tweaker mod needs
     *
     * @param configuration - mixin config resource name
     */
    @SuppressWarnings("unused")
    public static void removeMixin(String configuration) {
        if (configuration == null || configuration.trim().isEmpty()) {
            LOGGER.warn("TwelvefoldRegistryAPI supplied null or empty configuration name for mixin removal, ignoring.");
            return;
        }
        LOGGER.info("TwelvefoldRegistryAPI supplied \"" + configuration + "\" for mixin removal, adding.");
        rejectMixins.add(configuration);
    }

    /**
     * Internal Use; Do Not Use
     *
     * @return earlyMixins
     */
    public static HashMap<String, List<Supplier<Boolean>>> getEarlyMixins() {
        return earlyMixins;
    }

    /**
     * Internal Use; Do Not Use
     *
     * @return lateMixins
     */
    public static HashMap<String, List<Supplier<Boolean>>> getLateMixins() {
        return lateMixins;
    }

    /**
     * Internal Use; Do Not Use
     *
     * @return rejectMixins
     */
    public static List<String> getRejectMixins() {
        return rejectMixins;
    }

    /**
     * Internal Use; Do Not Use
     */
    public static void clear() {
        // :)
        earlyMixins = null;
        lateMixins = null;
        rejectMixins = null;
    }
    public static boolean isClassLoaded(String className)
    {
        return loadedClasses.contains(className);
    }
    public static boolean isPackageLoaded(String packageName)
    {
        for(String className:loadedClasses)
        {
            if(className.startsWith(packageName))
                return true;
        }
        return false;
    }
}