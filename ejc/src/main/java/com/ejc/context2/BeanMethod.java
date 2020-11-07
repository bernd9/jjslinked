package com.ejc.context2;

import lombok.NonNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

class BeanMethod extends SingletonProvider {

    private String name;

    public BeanMethod(@NonNull String name, @NonNull ClassReference returnType, @NonNull List<ClassReference> parameterTypes) {
        super(returnType, parameterTypes);
        this.name = name;
    }

    public Object invoke(Object configuration) {
        try {
            Method method = configuration.getClass().getDeclaredMethod(name, parameterTypes());
            method.setAccessible(true);
            Object rv = method.invoke(configuration, parameters());
            Objects.requireNonNull(rv, "Method " + method + " returned null");
            return rv;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
