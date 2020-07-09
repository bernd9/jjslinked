package com.ejc.processor;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Constructor;

@RequiredArgsConstructor
public class SingeltonLoaderBase {

    private final String classname;

    Object load() {
        return null;
    }

    private static Object createInstance(Class<?> c) {
        try {
            Constructor constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

    // TODO könnte gelöscht worden sein, oder ?
    private static Class<?> classForName(String className) throws ClassNotFoundException {
        return ClassLoader.getSystemClassLoader().loadClass(className);
    }

}
