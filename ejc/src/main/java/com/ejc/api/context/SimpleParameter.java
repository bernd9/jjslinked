package com.ejc.api.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class SimpleParameter implements Parameter {
    private final ClassReference parameterType;

    @Getter
    private Object value;

    @Override
    public boolean onSingletonCreated(Object o) {
        if (value != null) {
            return true;
        }
        if (parameterType.isInstance(o)) {
            if (value != null) {
                // TODO Exception
            }
            value = o;
            return true;
        }
        return false;
    }

    @Override
    public boolean isSatisfied() {
        return value != null;
    }
}
