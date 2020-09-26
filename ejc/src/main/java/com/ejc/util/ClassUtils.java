package com.ejc.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassUtils {

    // TODO: Fails, fix or remove
    public static <T> Set<Class<T>> getLoadedClasses(Class<T> type) {
        return Arrays.stream(InstrumentationHook.getInstrumentation().getAllLoadedClasses())
                .filter(c -> type.isAssignableFrom(c))
                .map(c -> (Class<T>) c)
                .collect(Collectors.toSet());
    }

    public static Object createInstance(String c) {
        return createInstance(classForName(c));
    }

    public static <T> T createInstance(Class<T> c) {
        try {
            Constructor constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            return (T) constructor.newInstance();
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

    public static Class<?> classForName(String className) {
        //
        try {
            //return Class.forName(className);
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Object p = InstrumentationHook.getInstrumentation().getAllLoadedClasses();
    }

}
