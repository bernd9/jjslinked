package com.ejc.api.context;

import com.ejc.context2.ClassReference;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
abstract class SingletonCollection {
    private final ClassReference elementType;
    private final Set<SingletonProvider> singletonProviders = new HashSet<>();

    void register(Collection<SingletonProvider> allConstructors) {
        singletonProviders.addAll(allConstructors.stream()
                .filter(c -> c.getType().isOfType(elementType))
                .collect(Collectors.toSet()));
    }

    void remove(SingletonProvider provider) {
        singletonProviders.remove(provider);
    }

}
