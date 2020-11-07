package com.ejc.api.context;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class SingletonProviders {
    private final Set<SingletonProvider> singletonProviders = new HashSet<>();

    void addProviders(Collection<? extends SingletonProvider> providers) {
        singletonProviders.addAll(providers);
    }

    boolean hasMatchingSourceFor(ClassReference classReference) {
        return singletonProviders.stream().anyMatch(provider -> classReference.isOfType(provider.getType()));
    }
}
