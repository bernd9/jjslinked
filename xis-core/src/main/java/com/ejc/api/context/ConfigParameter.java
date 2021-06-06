package com.ejc.api.context;

import one.xis.config.Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Map;


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
            Config config = Config.getInstance();
            if (Collection.class.isAssignableFrom(parameterType.getReferencedClass())) {
                Class<?> genericType = parameterType.getGenericType().map(ClassReference::getReferencedClass).orElseThrow(() -> new IllegalStateException("collection must have generic type"));
                Class<? extends Collection> collectionType = (Class<? extends Collection>) parameterType.getReferencedClass();
                value = config.getCollectionProperty(key, collectionType, genericType, defaultValue, mandatory);
            } else if (Map.class.isAssignableFrom(parameterType.getReferencedClass())) {
                Class<?> keyType = parameterType.getGenericType().map(ClassReference::getReferencedClass).orElseThrow(() -> new IllegalStateException("map must have generic key-type"));
                Class<?> valueType = parameterType.getGenericType2().map(ClassReference::getReferencedClass).orElseThrow(() -> new IllegalStateException("map must have generic value-type"));
                Class<? extends Map<Object, Object>> mapType = (Class<? extends Map<Object, Object>>) parameterType.getReferencedClass();
                value = config.getMapProperty(key, mapType, keyType, valueType, defaultValue, mandatory);
            } else {
                value = config.getInstance().getProperty(key, parameterType.getReferencedClass(), defaultValue, mandatory);
            }
        }
        return value;
    }

    @Override
    public void onSingletonCreated(Object o) {

    }
}
