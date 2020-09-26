package com.ejc.processor.model;

public interface ConstructorParameter {

    boolean isSatisfied();

    void setEventBus(SingletonCreationEventBus bus);
}
