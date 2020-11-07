package com.ejc.context2;

import com.ejc.api.context.ClassReference;

class CollectionParameter implements Parameter {

    private final ClassReference parameterType;

    CollectionParameter(ClassReference parameterType) {
        this.parameterType = parameterType;
    }

    @Override
    public boolean isSatisfied(SingletonProviders providers) {
        return false;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void onSingletonCreated(Object o) {

    }
}
