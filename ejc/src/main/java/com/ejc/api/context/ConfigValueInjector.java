package com.ejc.api.context;

import com.ejc.api.config.Config;
import com.ejc.util.FieldUtils;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.function.Function;

@RequiredArgsConstructor
class ConfigValueInjector {

    private final ClassReference declaringClass;
    private final String fieldName;
    private final Class<?> fieldType;
    private final String key;
    private final String defaultValue;

    void doInject(Function<Class<?>, Set<?>> selectFunction) {
        selectFunction.apply(declaringClass.getReferencedClass()).forEach(bean -> doInject(bean));
    }

    private void doInject(Object bean) {
        try {
            Field field = FieldUtils.getField(bean, fieldName);
            field.setAccessible(true);
            field.set(bean, Config.getProperty(key, fieldType, defaultValue));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
