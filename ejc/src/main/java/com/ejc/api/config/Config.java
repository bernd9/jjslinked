package com.ejc.api.config;

import com.ejc.api.profile.ActiveProfile;
import com.ejc.util.TypeUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Config {


    static void unload() {
        // TODO
    }

    public static <T> T getProperty(String path, Class<T> type, String defaultValue, boolean mandatory) throws PropertyNotFoundException {
        T property = getYamlConfiguration()
                .findSingleValue(path, type)
                .orElseGet(() -> convertDefaultValue(defaultValue, type, path));
        if (property == null && mandatory) {
            throw new PropertyNotFoundException(path);
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

    public static <T, C extends Collection> C getCollectionProperty(String path, Class<C> collectionType, Class<T> elementType, String defaultValue, boolean mandatory) throws PropertyNotFoundException {
        Collection<T> coll = getYamlConfiguration().findCollectionValue(path, collectionType, elementType);
        if (coll.isEmpty()) {
            if (defaultValue.isEmpty()) {
                if (mandatory) {
                    throw new PropertyNotFoundException(path);
                }
            } else {
                coll = getDefaultValueCollection(defaultValue, elementType);
            }
        }
        return (C) coll;
    }

    public static <K, V> Map<K, V> getMapProperty(String path, Class<? extends Map<K, V>> fieldType, Class<K> keyType, Class<V> valueType, String defaultValue, boolean mandatory) {
        Optional<Map<K, V>> map = getYamlConfiguration().findMapValue(path, keyType, valueType);
        if (!map.isPresent() && mandatory) {
            if (!defaultValue.isEmpty()) {
                throw new IllegalStateException("'defaultValue' can not be used for fields of type java.util.Map in field " + path);
            }
            if (mandatory) {
                throw new PropertyNotFoundException(path);
            }
        }
        return map.get();
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