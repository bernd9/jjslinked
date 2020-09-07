package com.ejc.api.config;

import com.ejc.api.profile.Profile;
import com.ejc.util.TypeUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Config {

    private static Properties properties;

    static {
        properties = loadProperties();
    }

    private static Properties loadProperties() {
        Properties properties = loadProperties("application.properties");
        String profile = Profile.getCurrentProfile();
        if (!profile.equals(Profile.DEFAULT_PROFILE)) {
            properties.putAll(loadProperties(String.format("application-%s.properties", profile)));
        }
        properties.putAll(loadSystemProperties());
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

    private static Map<String, String> loadSystemProperties() {
        return System.getenv();
    }

    public static <T> T getProperty(String name, Class<T> type) throws PropertyNotFoundException {
        String property = properties.getProperty(name);
        if (property == null) {
            throw new PropertyNotFoundException(name);
        }
        try {
            return TypeUtils.convertSimple(property, type);
        } catch (IllegalArgumentException e) {
            throw new IllegalPropertyTypeException(name, property, type);
        }
    }
}
