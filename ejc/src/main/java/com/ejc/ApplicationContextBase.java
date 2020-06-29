package com.ejc;

import lombok.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationContextBase {

    private Map<Class<?>, Object> beans = new HashMap<>();

    public ApplicationContextBase() {
        beans.put(getClass(), this);
    }

    public <T> T getBean(Class<T> c) {
        List<Object> result = new ArrayList<>(getBeans(c));
        switch (result.size()) {
            case 0:
                throw new IllegalArgumentException("no bean of type " + c.getName());
            case 1:
                return (T) result.get(0);
            default:
                throw new IllegalStateException("not unique: " + c.getName());
        }
    }

    private <T> T getBeanToInject(String className) {
        try {
            return (T) getBean(classForName(className));
        } catch (ClassNotFoundException e) {
            // never allow dependency to be null
            throw new RuntimeException("no such bean: " + className);
        }
    }

    public <T> Set<T> getBeans(Class<T> c) {
        return beans.entrySet().stream()
                .filter(e -> c.isAssignableFrom(e.getKey()))
                .map(Map.Entry::getValue)
                .map(c::cast)
                .collect(Collectors.toSet());
    }

    private <T> Set<T> getBeans(String c) {
        try {
            return (Set<T>) getBeans(classForName(c));
        } catch (ClassNotFoundException e) {
            // May happen
            return Collections.emptySet();
        }
    }


    @SuppressWarnings("unused")
    protected void add(String className) {
        Class<?> c = null;
        try {
            c = classForName(className);
            Object o = createInstance(c);
            beans.put(c, o);
        } catch (ClassNotFoundException e) {
            // May happen for deleted classes
        }

    }

    @SuppressWarnings("unused")
    protected void inject(String beanClass, String fieldName, String valueClass) {
        doInjectInTypeHirarachy(getBeans(beanClass), fieldName, getBeanToInject(valueClass));
    }

    @SuppressWarnings("unused")
    protected void injectAll(String beanClass, String fieldName, String valueClass) {
        doInjectInTypeHirarachy(getBeans(beanClass), fieldName, getBeans(valueClass));
    }

    @SuppressWarnings("unused")
    protected void invokeInitMethod(String beanClass, String methodName) {
        doInvokeInTypeHierarchy(getBeans(beanClass), methodName);
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


    private void doInjectInTypeHirarachy(@NonNull Set<Object> beans, @NonNull String fieldName, @NonNull Object value) {
        beans.forEach(bean -> doInject(bean, fieldName, value));
    }


    private void doInject(@NonNull Object bean, @NonNull String fieldName, @NonNull Object value) {
        getField(bean, fieldName).filter(field -> field.getType().isAssignableFrom(value.getClass())).ifPresent(field -> {
            field.setAccessible(true);
            try {
                field.set(bean, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private Optional<Field> getField(Object bean, String fieldName) {
        Class<?> c = bean.getClass();
        while (c != null) {
            try {
                Field field = c.getDeclaredField(fieldName);
                return Optional.of(field);
            } catch (NoSuchFieldException e) {
            }
            c = c.getSuperclass();
        }
        return Optional.empty();
    }


    private static Object createInstance(Class<?> c) {
        try {
            Constructor constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

    // TODO könnte gelöscht worden sein, oder ?
    private static Class<?> classForName(String className) throws ClassNotFoundException {
        return ClassLoader.getSystemClassLoader().loadClass(className);
    }
}
