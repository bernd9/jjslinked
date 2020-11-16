package com.ejc.api.context;

import com.ejc.api.config.Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
class ConfigParameter implements Parameter {

    @Getter
    private final ClassReference parameterType;
    private final String key;
    private final String defaultValue;
    private final boolean mandatory;
    private Object value;


    @Override
    public boolean isSatisfied(SingletonProviders providers) {
        return true;
    }

    @Override
    public Object getValue() {
        if (value == null) { // Read it lately, for better testing
            value = Config.getProperty(key, parameterType.getReferencedClass(), defaultValue, mandatory);
        }
        return value;
    }

    @Override
    public void onSingletonCreated(Object o) {

    }
}
