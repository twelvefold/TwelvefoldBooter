package twelvefold.twelvefoldbooter.misc;

import twelvefold.twelvefoldbooter.api.LateMixinLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.function.Predicate;

public class TwelvefoldMisc {
    public static final Random random=new Random();
    public static Predicate<String> getStringPredicate(LateMixinLoader lateMixinLoader, Class<?> clazz) throws NoSuchMethodException {
        Predicate<String >shouldMixinConfigQueue=x->true;
        String methodName= lateMixinLoader.shouldMixinConfigQueue();
        if(!methodName.isEmpty())
        {
            Method method= clazz.getMethod(methodName,String.class);
            shouldMixinConfigQueue=x-> {
                try {
                    return (boolean) method.invoke(null,x);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        }
        return shouldMixinConfigQueue;
    }
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        byte[] buffer=new byte[4096];
        int read;
        while ((read=inputStream.read(buffer))!=-1)
        {
            byteArrayOutputStream.write(buffer,0,read);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
