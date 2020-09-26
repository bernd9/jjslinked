package com.ejc.processor.model;

import com.ejc.api.context.ClassReference;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SingletonConstructor {
    private final List<ConstructorParameter> parameters;
    private SingletonCreationEventBus bus;

    public SingletonConstructor(ClassReference... parameterTypes) {
        final SingletonConstructor singletonConstructor = this;
        parameters = Arrays.stream(parameterTypes).map(classReference -> new ConstructorParameter(classReference, singletonConstructor)).collect(Collectors.toList());
    }


    void setEventBus(SingletonCreationEventBus bus) {
        this.bus = bus;
        parameters.forEach(parameter -> parameter.setEventBus(bus));
    }


    void onParameterSatisfied() {
        if (allParametersSatisfied()) {
            bus.singletonCreated(invoke());
        }
    }

    private Object invoke() {
        return null;
    }

    private boolean allParametersSatisfied() {
        return parameters.stream().noneMatch(param -> !param.isSatisfied());
    }

}
