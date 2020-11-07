package com.ejc.api.context;

import com.ejc.util.TypeUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
abstract class SingletonCollection {
    private final ClassReference elementType;

    SingletonCollection(Class<? extends Collection> collectionType) {
        elementType = ClassReference.getRef(TypeUtils.getGenericType(collectionType).getName());
    }

    boolean isSatisfied(SingletonProviders providers) {
        return !providers.hasMatchingSourceFor(elementType);

    }

}
