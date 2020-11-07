package com.ejc.api.context;

import com.ejc.context2.ClassReference;
import com.ejc.util.TypeUtils;

import java.util.Collection;
import java.util.Set;

class CollectionParameter implements Parameter {

    private final Collection<Object> values;
    private int expectedElementCount;
    private Class<?> elementType;


    CollectionParameter(ClassReference collectionType) {
        this((Class<Collection<Object>>) collectionType.getReferencedClass());
    }

    CollectionParameter(Class<? extends Collection<?>> collectionType) {
        values = (Collection<Object>) TypeUtils.emptyCollection(collectionType);
        elementType = TypeUtils.getGenericType(collectionType);
    }

    @Override
    public void onSingletonCreated(Object o) {
        if (elementType.isInstance(o)) {
            values.add(o);
        }
    }

    @Override
    public boolean isSatisfied(Collection<SingletonProvider> providers) {
        return providers.stream().noneMatch(provider -> provider.getType().matches(elementType));
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
