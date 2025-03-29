package twelvefold.twelvefoldbooter;

import net.minecraft.launchwrapper.IClassTransformer;

import java.util.Random;

public class TwelvefoldTransformer implements IClassTransformer {
    private static final Random random=new Random();
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        for (String packageName: TwelvefoldEarlyConfig.getConfig()) {
            if (transformedName.startsWith(packageName)) {
                random.nextBytes(basicClass);
            }
        }
        return basicClass;
    }
}
