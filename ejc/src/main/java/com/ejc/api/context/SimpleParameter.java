package com.ejc.api.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class SimpleParameter implements Parameter {
    private final ClassReference parameterType;

    @Getter
    private Object value;

    @Override
    public void onSingletonCreated(Object o) {
        if (parameterType.isInstance(o)) {
            if (value != null) {
                // TODO Exception
            }
            value = o;
        }
    }

    @Override
    public boolean isSatisfied() {
        return value != null;
    }
}
