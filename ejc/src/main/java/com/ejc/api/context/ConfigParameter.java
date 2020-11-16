package com.ejc.api.context;

import com.ejc.Value;
import com.ejc.api.config.Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Executable;


@RequiredArgsConstructor
class ConfigParameter implements Parameter {

    @Getter
    private final ClassReference parameterType;
    private final int paramIndex;
    private final Executable executable;
    private Object value;

    @Override
    public boolean isSatisfied(SingletonProviders providers) {
        return true;
    }

    @Override
    public Object getValue() {
        if (value == null) {
            Value valueAnnotation = executable.getParameters()[paramIndex].getAnnotation(Value.class);
            value = Config.getProperty(valueAnnotation.key(), parameterType.getReferencedClass(), valueAnnotation.defaultValue());
        }
        return value;
    }

    @Override
    public void onSingletonCreated(Object o) {
        
    }
}
