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

    public static Object getFieldValue(Object o, String fieldName) {
        Field field = getField(o, fieldName);
        return getFieldValue(o, field);
    }

    public static Object getFieldValue(Object o, Field field) {
        field.setAccessible(true);
        try {
            return field.get(o);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getFieldValue(Object o, String fieldName, Class<T> fieldType) {
        return fieldType.cast(getFieldValue(o, fieldName));
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
