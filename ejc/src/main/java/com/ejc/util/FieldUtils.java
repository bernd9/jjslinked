package com.ejc.util;

import java.lang.reflect.Field;

public class FieldUtils {

    public static Field getField(Object bean, String fieldName) {
        for (Class<?> c = bean.getClass(); c != null && !c.equals(Object.class); c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
            }
        }
        throw new RuntimeException(bean.getClass() + ": no such field: " + fieldName); // TODO Exceptionclass
    }

    public static void setFieldValue(Object bean, Field field, Object value) {
        field.setAccessible(true);
        try {
            field.set(bean, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setFieldValue(Object bean, String fieldName, Object value) {
        Field field = getField(bean, fieldName);
        setFieldValue(bean, field, value);
    }
}
