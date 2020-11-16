package com.ejc.api.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.util.List;

class SingletonConstructor extends SingletonProvider {

    public SingletonConstructor(ClassReference type, List<ClassReference> parameterTypes) {
        super(type, parameterTypes);
        initParameters();
    }

    @Override
    Object provide() {
        try {
            Constructor<?> constructor = getType().getReferencedClass().getDeclaredConstructor(parameterTypes());
            constructor.setAccessible(true);
            return constructor.newInstance(parameters());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Executable lookupExecutable() {
        try {
            return getType().getReferencedClass().getDeclaredConstructor(parameterTypes());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
