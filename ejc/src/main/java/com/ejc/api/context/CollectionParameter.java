package com.ejc.api.context;

import com.ejc.util.TypeUtils;
import lombok.Getter;

import java.util.Collection;

class CollectionParameter extends SingletonCollection implements Parameter {

    @Getter
    private final ClassReference parameterType;
    private final Collection<Object> values;

    CollectionParameter(ClassReference collectionType) {
        super((Class<? extends Collection<Object>>) collectionType.getReferencedClass());
        values = TypeUtils.emptyCollection((Class<? extends Collection<Object>>) collectionType.getReferencedClass());
        this.parameterType = collectionType;
    }

    @Override
    public boolean isSatisfied(SingletonProviders providers) {
        return !providers.hasMatchingSourceFor(getElementType());
    }

    @Override
    public Object getValue() {
        return values;
    }

    @Override
    public void onSingletonCreated(Object o) {
        if (getElementType().isInstance(o)) {
            values.add(o);
        }
    }
}
