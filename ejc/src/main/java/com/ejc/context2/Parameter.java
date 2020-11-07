package com.ejc.context2;

interface Parameter {

    boolean isSatisfied(SingletonProviders providers);

    Object getValue();

    void onSingletonCreated(Object o);
}
