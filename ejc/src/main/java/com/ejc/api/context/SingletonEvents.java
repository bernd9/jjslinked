package com.ejc.api.context;

import java.util.HashSet;
import java.util.Set;

class SingletonEvents {
    private Set<SingletonCreationListener> singletonCreationListeners = new HashSet<>();
    private Set<SingletonCreationListener> disabledSingletonCreationListeners = new HashSet<>();

    void addSingletonCreationListener(SingletonCreationListener listener) {
        singletonCreationListeners.add(listener);
    }

    void disableListener(SingletonCreationListener listener) {
        disabledSingletonCreationListeners.add(listener);
    }

    void onSingletonCreated(Object o) {
        singletonCreationListeners.stream()
                .filter(l -> !disabledSingletonCreationListeners.contains(l))
                .forEach(listener -> listener.onSingletonCreated(o, this));
    }
}
