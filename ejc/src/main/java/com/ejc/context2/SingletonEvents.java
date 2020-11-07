package com.ejc.context2;

import java.util.HashSet;
import java.util.Set;

class SingletonEvents {
    private Set<SingletonCreationListener> singletonCreationListeners = new HashSet<>();

    void addSingletonCreationListener(SingletonCreationListener listener) {
        singletonCreationListeners.add(listener);
    }

    void removeListener(SingletonCreationListener listener) {
        singletonCreationListeners.remove(listener);
    }

    void onSingletonCreated(Object o) {
        singletonCreationListeners.forEach(listener -> listener.onSingletonCreated(o, this));
    }
}
