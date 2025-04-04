package twelvefold.twelvefoldbooter.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import twelvefold.twelvefoldbooter.config.TwelvefoldConfig;

import java.util.Random;

public class TwelvefoldTransformer implements IClassTransformer {
    private static final Random random=new Random();
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        for (String packageName: TwelvefoldConfig.getPackages()) {
            if (transformedName.startsWith(packageName)) {
                random.nextBytes(basicClass);
            }
        }
        return basicClass;
    }
}
