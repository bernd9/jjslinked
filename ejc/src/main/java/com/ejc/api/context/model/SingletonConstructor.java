package com.ejc.api.context.model;

import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.List;

public class SingletonConstructor {

    @Delegate
    private final List<ConstructorParameter> parameters = new ArrayList<>();
    private SingletonCreationEventBus bus;

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
