package com.ejc.context2;

import java.util.HashSet;
import java.util.Set;

class SingletonEvents {
    private Set<SingletonCreationListener> listeners = new HashSet<>();

    void addListener(SingletonCreationListener listener) {
        listeners.add(listener);
    }

    void removeListener(SingletonCreationListener listener) {
        listeners.remove(listener);
    }

    void onSingletonCreated(Object o) {
        listeners.forEach(listener -> listener.onSingletonCreated(o));
    }
}
