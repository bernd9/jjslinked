package com.ejc.processor;

import com.ejc.ApplicationContext;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;

@RequiredArgsConstructor
public class InjectorBase {
    private final String declaringClassName;
    private final String fieldName;
    private final String fieldType;

    public void doInject(ApplicationContext context) {
        try {
            Class<?> declaringClass = InstanceUtils.classForName(declaringClassName);
            Object fieldValue = context.getBean(fieldType);
            context.getBeans(declaringClass).forEach(bean -> doInject(bean, declaringClass, fieldValue));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void doInject(Object bean, Class<?> declaringClass, Object fieldValue) {
        Class<?> clazz = bean.getClass();
        while (clazz != null && clazz.equals(Object.class)) {
            if (clazz.equals(declaringClass)) {
                try {
                    doInject(bean, clazz.getDeclaredField(fieldName), fieldValue);
                    break;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
            clazz = clazz.getSuperclass();
        }
    }

    private void doInject(Object bean, Field field, Object fieldValue) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(bean, fieldValue);
    }
}
