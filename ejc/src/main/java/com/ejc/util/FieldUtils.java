package com.ejc.util;

import java.lang.reflect.Field;

public class FieldUtils {
    
    public static Field getField(Object bean, String fieldName) throws NoSuchFieldException {
        for (Class<?> c = bean.getClass(); c != null && !c.equals(Object.class); c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
            }
        }
        throw new NoSuchFieldException(fieldName);
    }
}
