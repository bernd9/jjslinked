package com.ejc.api.context;

import com.ejc.api.config.Config;
import com.ejc.util.FieldUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
        FieldUtils.setFieldValue(bean, fieldName, Config.getProperty(key, fieldType, defaultValue));
    }
}
