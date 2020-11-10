package com.ejc.api.context;

import java.util.HashSet;
import java.util.Set;

class SingletonEvents {
    private Set<SingletonCreationListener> singletonCreationListeners = new HashSet<>();

    void addSingletonCreationListener(SingletonCreationListener listener) {
        singletonCreationListeners.add(listener);
    }

    void onSingletonCreated(Object o) {
        singletonCreationListeners.stream()
                .filter(l -> !l.isDisabled())
                .forEach(listener -> listener.onSingletonCreated(o, this));
    }
}
