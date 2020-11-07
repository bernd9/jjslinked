package com.ejc.api.context;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

class SingletonProviders {
    private Collection<SingletonProvider> providers = new HashSet<>();

    void addProviders(Collection<SingletonProvider> providers) {
        this.providers.addAll(providers);
    }

    void instantiate() {
        Set<SingletonProvider> satisfiedProviders = providers.stream()
                .filter(provider -> provider.isSatisfied(providers))
                .collect(Collectors.toSet());
        providers.removeAll(satisfiedProviders);
        satisfiedProviders.stream()
                .map(SingletonProvider::create)
                .forEach(this::beanCreated);
    }


    private void beanCreated(Object o) {
        providers.forEach(provider -> provider.onSingletonCreated(o));
        instantiate();
    }
}
