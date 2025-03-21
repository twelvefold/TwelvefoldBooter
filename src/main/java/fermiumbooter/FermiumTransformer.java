package fermiumbooter;

import net.minecraft.launchwrapper.IClassTransformer;

import java.util.Random;

public class FermiumTransformer implements IClassTransformer {
    private static final Random random=new Random();
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        for (String packageName:EarlyConfig.getConfig()) {
            if (transformedName.startsWith(packageName)) {
                random.nextBytes(basicClass);
            }
        }
        return basicClass;
    }
}
