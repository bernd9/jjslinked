package com.ejc.api.context;

import com.ejc.api.config.Config;
import com.ejc.util.FieldUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class ConfigField {
    private final ClassReference declaringClass;
    private final String fieldName;
    private final Class<?> fieldType;
    private final Optional<Class<?>> genericType;
    private final Optional<Class<?>> genericType2;
    private final String key;
    private final String defaultValue;
    private final boolean mandatory;

    public void injectConfigValue(Object bean) {
        Config config = Config.getInstance();
        if (Collection.class.isAssignableFrom(fieldType)) {
            Class genType = genericType.orElseThrow(() -> new IllegalStateException(declaringClass.getClassName() + "." + fieldName + " must have generic type"));
            FieldUtils.setFieldValue(bean, fieldName, config.getCollectionProperty(key, (Class<? extends Collection<?>>) fieldType, genType, defaultValue, mandatory));
        } else if (Map.class.isAssignableFrom(fieldType)) {
            Class keyType = genericType.orElseThrow(() -> new IllegalStateException(declaringClass.getClassName() + "." + fieldName + " must have generic key-type"));
            Class valueType = genericType.orElseThrow(() -> new IllegalStateException(declaringClass.getClassName() + "." + fieldName + " must have generic value-type"));
            FieldUtils.setFieldValue(bean, fieldName, config.getMapProperty(key, (Class<? extends Map<Object, Object>>) fieldType, keyType, valueType, defaultValue, mandatory));
        } else {
            FieldUtils.setFieldValue(bean, fieldName, config.getInstance().getProperty(key, fieldType, defaultValue, mandatory));
        }
    }
}
