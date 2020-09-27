package com.ejc.api.context.model;

public interface ConstructorParameter {

    boolean isSatisfied();

    void setEventBus(SingletonCreationEventBus bus);
}
