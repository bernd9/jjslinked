package one.xis.config;

public class PropertyNotFoundException extends RuntimeException {
    PropertyNotFoundException(String propertyName) {
        super(String.format("Property not found: '%s'", propertyName));
    }
}
