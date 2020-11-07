package com.ejc.context2;

import com.ejc.api.context.ClassReference;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class SimpleParameter implements Parameter {

    private final ClassReference parameterType;

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
