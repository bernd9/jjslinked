package com.ejc.api.config;

import com.ejc.api.profile.ActiveProfile;
import com.ejc.util.TypeUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Config {


    static void unload() {
        // TODO
    }

    public static <T> T getProperty(String name, Class<T> type, String defaultValue, boolean mandatory) throws PropertyNotFoundException {
        List<String> path = Arrays.asList(name.split("\\."));
        T property = getYamlConfiguration()
                .findSingleValue(path, type)
                .orElseGet(() -> convertDefaultValue(defaultValue, type, name));
        if (property == null && mandatory) {
            throw new PropertyNotFoundException(name);
        }
        return property;
    }

    private static <T> T convertDefaultValue(String defaultValue, Class<T> type, String name) {
        if (defaultValue.isEmpty()) {
            return null;
        }
        try {
            return TypeUtils.convertStringToSimple(defaultValue, type);
        } catch (IllegalArgumentException e) {
            throw new IllegalPropertyTypeException(name, defaultValue, type);
        }
    }

    public static <T, C extends Collection> C getCollectionProperty(String name, Class<C> collectionType, Class<T> elementType, String defaultValue, boolean mandatory) throws PropertyNotFoundException {
        List<String> path = Arrays.asList(name.split("\\."));
        Collection<T> coll = getYamlConfiguration().findCollectionValue(path, collectionType, elementType);
        if (coll.isEmpty()) {
            if (defaultValue.isEmpty()) {
                if (mandatory) {
                    throw new PropertyNotFoundException(name);
                }
            } else {
                coll = getDefaultValueCollection(defaultValue, elementType);
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

    private static YamlConfiguration yamlConfiguration;

    private static YamlConfiguration getYamlConfiguration() {
        if (yamlConfiguration == null) {
            yamlConfiguration = new YamlConfiguration(ActiveProfile.getCurrentProfile());
            yamlConfiguration.init();
        }
        return yamlConfiguration;
    }
}