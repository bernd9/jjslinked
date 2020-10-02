package com.ejc.api.context.model;

import com.ejc.api.config.Config;
import com.ejc.api.context.ClassReference;
import com.ejc.util.FieldUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class ConfigValueField {
    private final ClassReference declaringClass;
    private final String fieldName;
    private final Class<?> fieldType;
    private final String key;
    private final String defaultValue;

    public void injectConfigValue(Object bean) {
        try {
            Field field = FieldUtils.getField(bean, fieldName);
            field.setAccessible(true);
            field.set(bean, Config.getProperty(key, fieldType, defaultValue));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
