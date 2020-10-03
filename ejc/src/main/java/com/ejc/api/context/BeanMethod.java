package com.ejc.api.context;

import lombok.NonNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

class BeanMethod extends SingletonProvider {

    private ClassReference owner;
    private String name;
    private Object configuration;

    public BeanMethod(@NonNull ClassReference owner, @NonNull String name, @NonNull ClassReference returnType, @NonNull List<ClassReference> parameterTypes) {
        super(returnType, parameterTypes);
        this.owner = owner;
        this.name = name;
    }

    @Override
    public void onSingletonCreated(Object o) {
        if (owner.isInstance(o)) {
            configuration = o;
        }
        super.onSingletonCreated(o);
    }

    @Override
    boolean isSatisfied() {
        return configuration != null && super.isSatisfied();
    }

    @Override
    protected Object create() {
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
