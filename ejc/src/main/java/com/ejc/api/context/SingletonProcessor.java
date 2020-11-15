package com.ejc.api.context;

import java.util.Optional;

public interface SingletonProcessor {

    default Optional<Object> beforeInstantiation(Class<?> type) {
        return Optional.empty();
    }

    default Optional<Object> afterInstantiation(Object o) {
        return Optional.empty();
    }
}
