package com.ejc.processor;

import com.ejc.ApplicationContext;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;

@RequiredArgsConstructor
public class SystemPropertyInjectorBase {
    private final String declaringClassName;
    private final String fieldName;
    private final String propertyName;
    private final String defaultValue;

    public void doInject(ApplicationContext context) {
        try {
            Class<?> declaringClass = BeanUtils.classForName(declaringClassName);
            Field field = declaringClass.getDeclaredField(fieldName);
            Object fieldValue = getSystemPropertyConverted(field.getType(), defaultValue);
            context.getBeans(declaringClass).forEach(bean -> doInject(bean, declaringClass, fieldValue));
        } catch (Exception e) {
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

    private Object getSystemPropertyConverted(Class<?> fieldType, String defaultValue) {
        String property = System.getProperty(propertyName);
        if (property == null) {
            if (defaultValue == null) {
                throw new IllegalStateException("nor system-property or default-value for property " + propertyName);
            }
            property = defaultValue;
        }
        if (property.isBlank()) {
            throw new IllegalStateException("\"\" is not valid for property " + propertyName);
        }
        if (fieldType == String.class) {
            return property;
        }
        if (fieldType == Short.class || fieldType == Short.TYPE) {
            return Short.parseShort(property);
        }
        if (fieldType == Integer.class || fieldType == Integer.TYPE) {
            return Integer.parseInt(property);
        }
        if (fieldType == Float.class || fieldType == Float.TYPE) {
            return Float.parseFloat(property);
        }
        if (fieldType == Double.class || fieldType == Double.TYPE) {
            return Double.parseDouble(property);
        }
        if (fieldType == Long.class || fieldType == Long.TYPE) {
            return Long.decode(property);
        }
        if (fieldType == BigInteger.class) {
            return new BigInteger(property);
        }
        if (fieldType == BigDecimal.class) {
            return new BigDecimal(property);
        }
        if (fieldType == Boolean.class || fieldType == Boolean.TYPE) {
            return Boolean.parseBoolean(property);
        }
        if (fieldType == Byte.class || fieldType == Byte.TYPE) {
            return Byte.parseByte(property);
        }
        if (fieldType == Character.class || fieldType == Character.TYPE) {
            if (property.length() != 1) {
                throw new IllegalArgumentException(fieldName + ": \"" + property + "\" is not a character");
            }
            return property.charAt(0);
        }
        throw new IllegalStateException("illegal fieldtype for " + fieldName);

    }
}
