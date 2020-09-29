package com.ejc.api.context.model;

import com.ejc.api.context.ClassReference;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SingletonConstructor {

    private final ClassReference classReference;

    @Delegate
    private final List<ConstructorParameter> parameters = new ArrayList<>();
    private SingletonCreationEvents events;

    void setEvents(SingletonCreationEvents events) {
        this.events = events;
        parameters.forEach(parameter -> parameter.setEvents(events));
    }

    void onParameterSatisfied() {
        if (allParametersSatisfied()) {
            try {
                invoke();
            } catch (Exception e) {
                throw new RuntimeException("unable to create " + classReference.getClassName());
            }
        }
    }

    void invoke() throws Exception {
        Constructor<?> constructor = getConstructor();
        constructor.setAccessible(true);
        events.singletonCreated(constructor.newInstance(getParameters()));
    }


    boolean allParametersSatisfied() {
        return parameters.stream().noneMatch(param -> !param.isSatisfied());
    }

    private Constructor<?> getConstructor() throws Exception {
        return classReference.getReferencedClass()
                .getDeclaredConstructor(getParameterTypes());
    }

    private Class<?>[] getParameterTypes() {
        return parameters.stream()
                .map(ConstructorParameter::getType)
                .map(Object.class::cast)
                .toArray(Class[]::new);
    }

    private Object[] getParameters() {
        return parameters.stream().map(ConstructorParameter::getValue).toArray(Object[]::new);
    }

}
