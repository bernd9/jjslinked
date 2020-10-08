package com.ejc.api.context;

import lombok.NonNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

class BeanMethod extends SingletonProvider {

    private ClassReference owner;
    private String name;
    private Object configuration;
    private boolean ownerFieldsInjected;
    private boolean parametersCompleted;

    public BeanMethod(@NonNull ClassReference owner, @NonNull String name, @NonNull ClassReference returnType, @NonNull List<ClassReference> parameterTypes) {
        super(returnType, parameterTypes);
        this.owner = owner;
        this.name = name;
    }

    @Override
    public boolean onSingletonCreated(Object o) {
        if (owner.isInstance(o)) {
            configuration = o;
        }
        parametersCompleted = super.onSingletonCreated(o);
        return configuration != null && parametersCompleted && ownerFieldsInjected;
    }

    boolean setOwnerFieldsInjected() {
        ownerFieldsInjected = true;
        return configuration != null && parametersCompleted;
    }

    @Override
    public boolean isSatisfied() {
        return configuration != null && parametersCompleted && ownerFieldsInjected;
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
