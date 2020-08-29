package com.ejc.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.BigInteger;

@UtilityClass
public class TypeUtils {

    public <T> T convertSimple(@NonNull String property, @NonNull Class<T> fieldType) {

        if (fieldType == String.class) {
            return (T) property;
        }
        if (fieldType == Short.class || fieldType == Short.TYPE) {
            return (T) Short.valueOf(property);
        }
        if (fieldType == Integer.class || fieldType == Integer.TYPE) {
            return (T) Integer.valueOf(property);
        }
        if (fieldType == Float.class || fieldType == Float.TYPE) {
            return (T) Float.valueOf(property);
        }
        if (fieldType == Double.class || fieldType == Double.TYPE) {
            return (T) Double.valueOf(property);
        }
        if (fieldType == Long.class || fieldType == Long.TYPE) {
            return (T) Long.decode(property);
        }
        if (fieldType == BigInteger.class) {
            return (T) new BigInteger(property);
        }
        if (fieldType == BigDecimal.class) {
            return (T) new BigDecimal(property);
        }
        if (fieldType == Boolean.class || fieldType == Boolean.TYPE) {
            return (T) Boolean.valueOf(property);
        }
        if (fieldType == Byte.class || fieldType == Byte.TYPE) {
            return (T) Byte.valueOf(property);
        }
        if (fieldType == Character.class || fieldType == Character.TYPE) {
            if (property.length() != 1) {
                throw new IllegalArgumentException(property + "\" is not a character");
            }
            return (T) Character.valueOf(property.charAt(0));
        }
        throw new IllegalStateException("illegal type for " + fieldType);

    }
}
