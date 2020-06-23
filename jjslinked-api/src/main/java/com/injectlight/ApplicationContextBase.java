package com.injectlight;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApplicationContextBase {

    private Map<Class<?>, Object> beans = new HashMap<>();


    public <T> T getBean(Class<?> c) {
        List<Object> result = beans.entrySet().stream()
                .filter(e -> c.isAssignableFrom(e.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        switch (result.size()) {
            case 0:
                throw new IllegalArgumentException("no such bean " + c.getName());
            case 1:
                return (T) result.get(0);
            default:
                throw new IllegalStateException("not unique: " + c.getName());
        }
    }


    protected Object add(String className) {
        Class<?> c = classForName(className);
        Object o = createInstance(c);
        beans.put(c, o);
        return o;
    }

    protected void inject(String beanClass, String fieldName, String valueClass) {
        try {
            doInject(getByClassName(beanClass), fieldName, getByClassName(valueClass));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void doInject(Object bean, String fieldName, Object value) throws Exception {
        Field field = bean.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(bean, value);
    }


    private Object getByClassName(String name) {
        return beans.entrySet().stream()
                .filter(e -> e.getKey().toString().equals(name))
                .findFirst().orElseThrow();
    }

    private static Object createInstance(Class<?> c) {
        try {
            return c.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> classForName(String className) {
        try {
            return Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
