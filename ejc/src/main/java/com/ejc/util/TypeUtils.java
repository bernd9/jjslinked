package com.ejc.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@UtilityClass
public class TypeUtils {

    public static final Map<String, Class<?>> PRIMITIVES = Map.of("byte", byte.class, //
            "short", short.class,//
            "int", int.class,//
            "long", long.class, //
            "float", float.class,//
            "double", double.class, //
            "boolean", boolean.class,//
            "char", char.class);


    public Optional<Class<?>> getPrimitiveClass(String primitive) {
        return Optional.ofNullable(PRIMITIVES.get(primitive));
    }

    public <T, C extends Collection<? extends T>> C emptyCollection(Class<C> collectionType) {
        if (!collectionType.isInterface() && !Modifier.isAbstract(collectionType.getModifiers())) {
            return ClassUtils.createInstance(collectionType);
        }
        if (collectionType.equals(List.class)) {
            return (C) new ArrayList<T>();
        }
        if (collectionType.equals(Set.class) || collectionType.equals(Collection.class)) {
            return (C) new HashSet<T>();
        }
        if (collectionType.equals(Vector.class)) {
            return (C) new Vector<T>();
        }
        throw new IllegalArgumentException("unsupported collection type ");

    }

    public <T> T convertSimple(Object value, @NonNull Class<T> fieldType) {
        if (value == null) {
            return null;
        }
        if (fieldType.isInstance(value)) {
            return (T) value;
        }
        return convertStringToSimple(value.toString(), fieldType);
    }

    public <T> T convertStringToSimple(String value, @NonNull Class<T> fieldType) {
        if (value == null) {
            if (fieldType.isPrimitive()) {
                throw new NullPointerException("null is not allowed for primitives");
            }
            return null;
        }
        if (fieldType == String.class) {
            return (T) value;
        }
        if (fieldType == Short.class || fieldType == Short.TYPE) {
            return (T) Short.valueOf(value);
        }
        if (fieldType == Integer.class || fieldType == Integer.TYPE) {
            return (T) Integer.valueOf(value);
        }
        if (fieldType == Float.class || fieldType == Float.TYPE) {
            return (T) Float.valueOf(value);
        }
        if (fieldType == Double.class || fieldType == Double.TYPE) {
            return (T) Double.valueOf(value);
        }
        if (fieldType == Long.class || fieldType == Long.TYPE) {
            return (T) Long.decode(value);
        }
        if (fieldType == BigInteger.class) {
            return (T) new BigInteger(value);
        }
        if (fieldType == BigDecimal.class) {
            return (T) new BigDecimal(value);
        }
        if (fieldType == Boolean.class || fieldType == Boolean.TYPE) {
            return (T) Boolean.valueOf(value);
        }
        if (fieldType == Byte.class || fieldType == Byte.TYPE) {
            return (T) Byte.valueOf(value);
        }
        if (fieldType == Character.class || fieldType == Character.TYPE) {
            if (value.length() != 1) {
                throw new IllegalArgumentException(value + "\" is not a character");
            }
            return (T) Character.valueOf(value.charAt(0));
        }
        throw new IllegalArgumentException("illegal type " + fieldType);

    }
}
