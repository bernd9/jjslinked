package com.ejc.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

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

    public static Collection<Field> getAllFields(Object o) {
        Map<String, Field> fields = new HashMap<>();
        getHierarchy(o.getClass()).stream()
                .map(Class::getDeclaredFields)
                .map(Arrays::asList)
                .flatMap(List::stream)
                .filter(field -> !Modifier.isFinal(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .forEach(field -> fields.put(field.getName(), field));
        return fields.values();
    }


    private static List<Class<?>> getHierarchy(Class<?> c) {
        List<Class<?>> classes = new ArrayList<>();
        while (c != null && c != Object.class) {
            classes.add(c);
            c = c.getSuperclass();
        }
        Collections.reverse(classes);
        return classes;
    }

    public static Optional<Class<?>> getGenericCollectionType(Field field) {
        if (!Collection.class.isAssignableFrom(field.getType())) {
            throw new IllegalArgumentException("not a collection field: " + field);
        }
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            return Optional.of((Class<?>) parameterizedType.getActualTypeArguments()[0]);
        }
        return Optional.empty();
    }
}
