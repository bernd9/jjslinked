package com.ejc.processor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Constructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InstanceUtils {

    public static Object createInstance(String c) {
        try {
            return createInstance(classForName(c));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object createInstance(Class<?> c) {
        try {
            Constructor constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

    public static Class<?> classForName(String className) throws ClassNotFoundException {
        //return Class.forName(className);
        return ClassLoader.getSystemClassLoader().loadClass(className);
    }

}
