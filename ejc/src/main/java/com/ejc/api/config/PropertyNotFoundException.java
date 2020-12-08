package com.ejc.api.config;

public class PropertyNotFoundException extends RuntimeException {
    PropertyNotFoundException(String propertyName) {
        super(String.format("Property not found: '%d'", propertyName));
    }
}
