package com.ejc.api.context;

import lombok.NonNull;

import java.util.List;
import java.util.Objects;

class BeanMethod extends SingletonProvider {

    private final String name;
    private final SingletonObject singletonObject;

    public BeanMethod(SingletonObject singletonObject, @NonNull String name, @NonNull ClassReference returnType, @NonNull List<ClassReference> parameterTypes) {
        super(returnType, parameterTypes);
        this.singletonObject = singletonObject;
        this.name = name;
    }

    @Override
    Object provide() {
        return invoke(singletonObject.getSingleton());
    }

    @Override
    public boolean isSatisfied(SingletonProviders providers) {
        return super.isSatisfied(providers) && singletonObject.isSatisfied();
    }

    @Override
    void onSingletonCreated(Object o) {
        super.onSingletonCreated(o);
    }

    private Object invoke(Object configuration) {
        try {
            var method = configuration.getClass().getDeclaredMethod(name, parameterTypes());
            method.setAccessible(true);
            var rv = method.invoke(configuration, parameters());
            Objects.requireNonNull(rv, "Method " + method + " returned null");
            return rv;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
