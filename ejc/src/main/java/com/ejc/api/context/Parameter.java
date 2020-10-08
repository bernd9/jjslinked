package com.ejc.api.context;

interface Parameter {

    boolean onSingletonCreated(Object o);

    boolean isSatisfied();

    Object getValue();
}
