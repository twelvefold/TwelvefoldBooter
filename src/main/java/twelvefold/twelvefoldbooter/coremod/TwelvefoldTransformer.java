package twelvefold.twelvefoldbooter.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import twelvefold.twelvefoldbooter.api.TwelvefoldRegistryAPI;
import twelvefold.twelvefoldbooter.config.TwelvefoldConfig;

import java.util.Random;

public class TwelvefoldTransformer implements IClassTransformer {
    private static final Random random=new Random();
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        TwelvefoldRegistryAPI.loadedClasses.add(transformedName);
        return basicClass;
    }
}
