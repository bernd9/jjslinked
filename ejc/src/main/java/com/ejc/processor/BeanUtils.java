package com.ejc.processor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanUtils {

    public static Object createInstance(String c) {
        try {
            return createInstance(classForName(c));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static Object createInstance(Class<?> c) {
        try {
            Constructor constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

    static Class<?> classForName(String className) throws ClassNotFoundException {
        //return Class.forName(className);
        return ClassLoader.getSystemClassLoader().loadClass(className);
    }

    private static Set<Field> getFields(Object bean, String fieldName, Class<? extends Annotation> annotationClass) {
        Set<Field> fields = new HashSet<>();
        Class<?> c = bean.getClass();
        while (c != null && c != Object.class) {
            try {
                Field field = c.getDeclaredField(fieldName);
                if (field.isAnnotationPresent(annotationClass)) {
                    fields.add(field);
                }
            } catch (NoSuchFieldException e) {
            }
            c = c.getSuperclass();
        }
        return fields;
    }


    private void doInvokeInTypeHierarchy(@NonNull Set<Object> beans, String method) {
        beans.forEach(bean -> doInvoke(bean, method));
    }

    private void doInvoke(Object bean, String methodName) {
        try {
            Method method = bean.getClass().getMethod(methodName);
            method.invoke(bean);
        } catch (NoSuchMethodException nsm) {
            // May happen when file is out of sync
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    static void doInjectInTypeHirarachy(@NonNull Set<Object> beans, @NonNull String fieldName, @NonNull Object value, Class<? extends Annotation> annotationClass) {
        beans.forEach(bean -> doInject(bean, fieldName, value, annotationClass));
    }


    private static void doInject(@NonNull Object bean, @NonNull String fieldName, @NonNull Object value, Class<? extends Annotation> annotationClass) {
        getFields(bean, fieldName, annotationClass).stream().filter(field -> field.getType().isAssignableFrom(value.getClass())).forEach(field -> {
            field.setAccessible(true);
            try {
                field.set(bean, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

    }

}
