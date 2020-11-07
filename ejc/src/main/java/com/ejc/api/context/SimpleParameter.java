package com.ejc.api.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
class SimpleParameter implements Parameter {
    private final ClassReference parameterType;

    @Getter
    private Object value;

    @Override
    public void onSingletonCreated(Object o) {
        if (value != null) {
            return;
        }
        if (parameterType.isInstance(o)) {
            if (value != null) {
                // TODO Exception
            }
            value = o;
        }
    }

    @Override
    public boolean isSatisfied(Collection<SingletonProvider> providers) {
        return value != null;
    }
}
