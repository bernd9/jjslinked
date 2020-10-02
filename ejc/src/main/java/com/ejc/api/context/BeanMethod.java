package com.ejc.api.context;

import lombok.NonNull;

import java.lang.reflect.Method;
import java.util.List;

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
    protected boolean isSatisfied() {
        return configuration != null && super.isSatisfied();
    }

    @Override
    protected Object create() {
        try {
            Method method = configuration.getClass().getDeclaredMethod(name, parameterTypes());
            method.setAccessible(true);
            return method.invoke(configuration, parameters());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
