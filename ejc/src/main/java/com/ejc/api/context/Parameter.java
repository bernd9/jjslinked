package com.ejc.api.context;

interface Parameter {

    boolean isSatisfied(SingletonProviders providers);

    Object getValue();

    void onSingletonCreated(Object o);
}
