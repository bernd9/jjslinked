package com.ejc.context2;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class SimpleParameter implements Parameter {

    private final ClassReference parameterType;
    private Object value;

    @Override
    public boolean isSatisfied(SingletonProviders providers) {
        return value != null;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void onSingletonCreated(Object o) {
        if (parameterType.isInstance(o)) {
            if (value != null) {
                // TODO throw exception
            }
            value = o;
        }
    }
}
