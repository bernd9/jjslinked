package com.ejc.context2;

import com.ejc.api.context.ClassReference;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;


class SingletonConstructor extends SingletonProvider implements SingletonCreationListener {

    private final List<Parameter> parameters = new ArrayList<>();

    private final SingletonEvents singletonEvents;
    private final SingletonProviders singletonProviders;

    public SingletonConstructor(ClassReference type, List<ClassReference> parameterTypes, SingletonEvents singletonEvents, SingletonProviders singletonProviders) {
        super(type, parameterTypes);
        this.singletonEvents = singletonEvents;
        this.singletonProviders = singletonProviders;
    }

    void invoke() {
        singletonEvents.removeListener(this);
        try {
            Constructor<?> constructor = getType().getReferencedClass().getDeclaredConstructor(parameterTypes());
            constructor.setAccessible(true);
            Object singleton = constructor.newInstance(parameters());
            singletonEvents.onSingletonCreated(singleton);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSingletonCreated(Object o) {
        if (updateParameters(o)) {
            invoke();
        }
    }

    private boolean updateParameters(Object o) {
        parameters.forEach(parameter -> parameter.onSingletonCreated(o));
        return parameters.stream()
                .noneMatch(parameter -> !parameter.isSatisfied(singletonProviders));
    }
}
