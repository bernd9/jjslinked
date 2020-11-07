package com.ejc.api.context;

import java.util.Collection;

interface Parameter {

    void onSingletonCreated(Object o);

    boolean isSatisfied(Collection<SingletonProvider> providers);

    Object getValue();
}
