package com.ejc.api.config;

public class PropertyNotFoundException extends RuntimeException {
    PropertyNotFoundException(String propertyName) {
        super("Property not found " + propertyName);
    }
}
