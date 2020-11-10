package com.ejc.api.context;

import lombok.Getter;

import java.lang.reflect.Constructor;
import java.util.List;


class SingletonConstructor extends SingletonProvider implements SingletonCreationListener {

    private SingletonProviders singletonProviders;
    @Getter
    private boolean disabled;

    public SingletonConstructor(ClassReference type, List<ClassReference> parameterTypes) {
        super(type, parameterTypes);
    }

    void invoke(SingletonEvents events) {
        if (disabled) {
            return;
        }
        disabled = true;
        try {
            Constructor<?> constructor = getType().getReferencedClass().getDeclaredConstructor(parameterTypes());
            constructor.setAccessible(true);
            Object singleton = constructor.newInstance(parameters());
            events.onSingletonCreated(singleton);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSingletonCreated(Object o, SingletonEvents events) {
        if (!disabled && updateParameters(o)) {
            invoke(events);
        }
    }

    private boolean updateParameters(Object o) {
        getParameters().forEach(parameter -> parameter.onSingletonCreated(o));
        return getParameters().stream()
                .noneMatch(parameter -> !parameter.isSatisfied(singletonProviders));
    }
}
