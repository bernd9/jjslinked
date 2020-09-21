package com.ejc.api.context;

import com.ejc.util.FieldUtils;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;

@RequiredArgsConstructor
abstract class InjectorBase {
    private final ClassReference declaringClass;
    private final String fieldName;
    private final ClassReference fieldType;

    void doInject(ApplicationContextFactoryBase factory) {
        factory.getBeans(declaringClass.getReferencedClass()).forEach(bean -> doInject(bean, factory));
    }

    private void doInject(Object bean, ApplicationContextFactoryBase factory) {
        try {
            doInjectFieldValue(bean, FieldUtils.getField(bean, fieldName), factory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doInjectFieldValue(Object bean, Field field, ApplicationContextFactoryBase factory) {
        try {
            field.setAccessible(true);
            field.set(bean, getFieldValue(fieldType.getReferencedClass(), factory));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    abstract Object getFieldValue(Class<?> fieldType, ApplicationContextFactoryBase factory);


}
