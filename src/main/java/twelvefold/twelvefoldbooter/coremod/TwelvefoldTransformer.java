package twelvefold.twelvefoldbooter.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import twelvefold.twelvefoldbooter.api.TwelvefoldRegistryAPI;

public class TwelvefoldTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        TwelvefoldRegistryAPI.loadedClasses.add(transformedName);
        return basicClass;
    }
}
