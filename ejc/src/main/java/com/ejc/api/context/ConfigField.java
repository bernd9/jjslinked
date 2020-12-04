package com.ejc.api.context;

import com.ejc.api.config.Config;
import com.ejc.util.FieldUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Optional;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class ConfigField {
    private final ClassReference declaringClass;
    private final String fieldName;
    private final Class<?> fieldType;
    private final Optional<Class<?>> genericType;
    private final String key;
    private final String defaultValue;
    private final boolean mandatory;

    public void injectConfigValue(Object bean) {
        if (genericType.isEmpty()) {
            FieldUtils.setFieldValue(bean, fieldName, Config.getProperty(key, fieldType, defaultValue, mandatory));
        } else {
            FieldUtils.setFieldValue(bean, fieldName, Config.getCollectionProperty(key, (Class<? extends Collection<?>>) fieldType, genericType.get(), defaultValue, mandatory));
        }
    }
}
