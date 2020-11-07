package com.ejc.api.context;

interface SingletonCreationListener {
    void onSingletonCreated(Object o, SingletonEvents events);
}
