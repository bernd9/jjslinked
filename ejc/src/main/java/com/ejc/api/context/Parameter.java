package com.ejc.api.context;

interface Parameter {

    void onSingletonCreated(Object o);

    boolean isSatisfied();

    Object getValue();
}
