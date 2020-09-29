package com.ejc.api.context.model;

public interface ConstructorParameter {

    boolean isSatisfied();

    void setEvents(SingletonCreationEvents events);

    Class<?> getType();

    Object getValue();
}
