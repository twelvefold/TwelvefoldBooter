package twelvefold.twelvefoldbooter.misc;

import twelvefold.twelvefoldbooter.api.LateMixinLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Predicate;

public class TwelvefoldMisc {
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
}
