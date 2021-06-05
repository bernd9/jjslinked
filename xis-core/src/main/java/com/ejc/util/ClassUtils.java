package com.ejc.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Constructor;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassUtils {


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

    public static Optional<String> getPackageName(String qualifiedName) {
        if (!qualifiedName.contains(".")) {
            return Optional.empty();
        }
        return Optional.of(qualifiedName.substring(0, qualifiedName.lastIndexOf(".")));
    }

    public static String getSimpleName(String qualifiedName) {
        if (!qualifiedName.contains(".")) {
            return qualifiedName;
        }
        return qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
    }
}
