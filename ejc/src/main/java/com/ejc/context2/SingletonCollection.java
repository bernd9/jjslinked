package com.ejc.context2;

import com.ejc.api.context.ClassReference;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
abstract class SingletonCollection {
    private final ClassReference elementType;

    boolean isSatisfied(SingletonProviders providers) {
        return !providers.hasMatchingSourceFor(elementType);

    }

}
