package com.ejc.api.context;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class SingletonConstructor extends SingletonProvider {

    public SingletonConstructor(ClassReference type, List<ClassReference> parameterTypes) {
        super(type, addEnclosingType(type, parameterTypes));
    }

    private static List<ClassReference> addEnclosingType(ClassReference type, List<ClassReference> parameterTypes) {
        List<ClassReference> types = new ArrayList<>(parameterTypes);
        if (type.getReferencedClass().getEnclosingClass() != null)
            types.add(ClassReference.getRef(type.getReferencedClass().getEnclosingClass().getName()));
        return types;
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
