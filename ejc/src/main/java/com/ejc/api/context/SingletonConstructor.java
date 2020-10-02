package com.ejc.api.context;

import java.lang.reflect.Constructor;
import java.util.List;

class SingletonConstructor extends SingletonProvider {

    public SingletonConstructor(ClassReference type, List<ClassReference> parameterTypes) {
        super(type, parameterTypes);
    }

    @Override
    protected Object create() {
        try {
            Constructor<?> constructor = getType().getReferencedClass().getDeclaredConstructor(parameterTypes());
            constructor.setAccessible(true);
            return constructor.newInstance(parameters());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
