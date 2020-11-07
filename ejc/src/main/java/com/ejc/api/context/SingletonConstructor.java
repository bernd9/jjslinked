package com.ejc.api.context;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;


class SingletonConstructor extends SingletonProvider implements SingletonCreationListener {

    private final List<Parameter> parameters = new ArrayList<>();
    private SingletonProviders singletonProviders;

    public SingletonConstructor(ClassReference type, List<ClassReference> parameterTypes) {
        super(type, parameterTypes);
    }

    void invoke(SingletonEvents events) {
        events.removeListener(this);
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
        if (updateParameters(o)) {
            invoke(events);
        }
    }

    private boolean updateParameters(Object o) {
        parameters.forEach(parameter -> parameter.onSingletonCreated(o));
        return parameters.stream()
                .noneMatch(parameter -> !parameter.isSatisfied(singletonProviders));
    }
}
