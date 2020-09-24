package com.ejc.context2;

import com.ejc.api.config.Config;
import com.ejc.api.context.ClassReference;
import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Field;

@Getter
@Builder
class ConfigValueField {
    private final ClassReference declaringClass;
    private final String fieldName;
    private final Class<?> fieldType;
    private final String key;
    private final String defaultValue;

    void doInject(Object bean) {
        try {
            Field field = getField(bean);
            field.setAccessible(true);
            field.set(bean, Config.getProperty(key, fieldType, defaultValue));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private Field getField(Object bean) throws NoSuchFieldException {
        Class<?> c = bean.getClass();
        while (!c.equals(Object.class)) {
            if (c.equals(declaringClass.getReferencedClass())) {
                try {
                    return c.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
            c = c.getSuperclass();
        }
        throw new NoSuchFieldException();
    }
}
