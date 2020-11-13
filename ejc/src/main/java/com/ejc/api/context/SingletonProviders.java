package com.ejc.api.context;

import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class SingletonProviders {
    @Getter
    private final Set<SingletonProvider> providers = new HashSet<>();

    void addProviders(Collection<? extends SingletonProvider> providers) {
        this.providers.addAll(providers);
    }

    boolean hasMatchingSourceFor(ClassReference classReference) {
        return providers.stream()
                .anyMatch(provider -> provider.getType().isOfType(classReference));
    }

    void remove(Collection<SingletonProvider> provider) {
        providers.removeAll(provider);
    }

    void onSingletonCreated(Object o) {
        providers.forEach(provider -> provider.onSingletonCreated(o));
    }
}
