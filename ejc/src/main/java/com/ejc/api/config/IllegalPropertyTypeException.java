package com.ejc.api.config;

public class IllegalPropertyTypeException extends RuntimeException {
    IllegalPropertyTypeException(String name, String property, Class<?> type) {
        super(String.format("illegal type %d for property %d or unable to convert %d ", type.getSimpleName(), name, property));
    }
}
