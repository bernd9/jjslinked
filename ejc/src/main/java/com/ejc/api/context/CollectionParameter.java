package com.ejc.api.context;

import com.ejc.util.TypeUtils;

import java.util.Collection;
import java.util.Set;

class CollectionParameter implements Parameter {

    private final Collection<?> values;
    private int expectedElementCount;
    private Class<?> elementType;


    CollectionParameter(ClassReference collectionType) {
        this((Class<Collection<Object>>) collectionType.getReferencedClass());
    }

    CollectionParameter(Class<? extends Collection<?>> collectionType) {
        values = TypeUtils.emptyCollection(collectionType);
        elementType = TypeUtils.getGenericType(collectionType);
    }

    @Override
    public void onSingletonCreated(Object o) {

    }

    @Override
    public boolean isSatisfied() {
        return false;
    }

    @Override
    public Object getValue() {
        return null;
    }

    void registerSingletonTypes(Set<ClassReference> types) {
        expectedElementCount = (int) types.stream()
                .filter(type -> type.getReferencedClass().isAssignableFrom(elementType))
                .count();
    }
}
