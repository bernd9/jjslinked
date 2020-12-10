package com.ejc.api.context;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class SingletonPreProcessor<T> {

    private final Class<T> type;

    boolean matches(Class<?> t) {
        return type.isAssignableFrom(t);
    }

    public Optional<T> beforeInstantiation(Class<T> type) {
        return Optional.empty();
    }

    public T afterInstantiation(T o) {
        return o;
    }
}
