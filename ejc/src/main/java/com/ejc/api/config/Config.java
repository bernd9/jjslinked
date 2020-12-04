package com.ejc.api.config;

import com.ejc.api.profile.ActiveProfile;
import com.ejc.util.TypeUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Config {

    private static Properties properties;

    private static Properties getProperties() {
        if (properties == null) {
            properties = loadProperties();
        }
        return properties;
    }

    static void unload() {
        properties = null;
    }

    private static Properties loadProperties() {
        Properties properties = loadProperties("application.properties");
        String profile = ActiveProfile.getCurrentProfile();
        if (!profile.equals(ActiveProfile.DEFAULT_PROFILE)) {
            properties.putAll(loadProperties(String.format("application-%s.properties", profile)));
        }
        properties.putAll(System.getenv());
        properties.putAll(System.getProperties());
        return properties;
    }


    private static Properties loadProperties(String resourceName) {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
            Properties properties = new Properties();
            if (in != null) {
                properties.load(in);
            }
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static <T> T getProperty(String name, Class<T> type, String defaultValue, boolean mandatory) throws PropertyNotFoundException {
        String property = getProperties().getProperty(name);
        if (property == null) {
            if (defaultValue.isEmpty() && mandatory)
                throw new PropertyNotFoundException(name);
            else
                property = defaultValue;
        }
        try {
            return TypeUtils.convertStringToSimple(property, type);
        } catch (IllegalArgumentException e) {
            throw new IllegalPropertyTypeException(name, property, type);
        }
    }

    public static <T, C extends Collection> C getCollectionProperty(String name, Class<C> collectionType, Class<T> elementType, String defaultValue, boolean mandatory) throws PropertyNotFoundException {
        Collection<T> coll = TypeUtils.emptyCollection(collectionType);
        for (int i = 0; ; i++) {
            String elementName = String.format("%s[%d]", name, i);
            String property = getProperties().getProperty(elementName);
            if (property == null) {
                break;
            }
            try {
                coll.add(TypeUtils.convertStringToSimple(property, elementType));
            } catch (IllegalArgumentException e) {
                throw new IllegalPropertyTypeException(name, property, elementType);
            }
        }

        if (coll.isEmpty()) {
            if (defaultValue.isEmpty()) {
                if (mandatory) {
                    throw new PropertyNotFoundException(name);
                }
            } else {
                coll.addAll(getDefaultValueCollection(defaultValue, elementType));
            }
        }
        return (C) coll;
    }

    private static <T> Set<T> getDefaultValueCollection(String defaultValueStr, Class<T> elementType) {
        return Arrays.stream(defaultValueStr.split(","))
                .map(String::trim)
                .filter(String::isEmpty)
                .map(s -> TypeUtils.convertStringToSimple(s, elementType))
                .collect(Collectors.toSet());
    }
}