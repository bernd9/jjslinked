package com.ejc.context2;

@FunctionalInterface
interface SingletonCreationListener {
    void onSingletonCreated(Object o);
}
