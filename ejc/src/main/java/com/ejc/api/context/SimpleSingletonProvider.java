package com.ejc.api.context;

import java.util.Collections;

class SimpleSingletonProvider extends SingletonProvider {

    private final Object singleton;

    public SimpleSingletonProvider(Object singleton) {
        super(ClassReference.getRef(singleton.getClass().getName()), Collections.EMPTY_LIST);
        this.singleton = singleton;
    }

    @Override
    Object invoke() {
        return singleton;
    }
}
