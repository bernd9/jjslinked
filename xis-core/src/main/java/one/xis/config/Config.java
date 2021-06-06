package one.xis.config;

import com.ejc.api.profile.ActiveProfile;
import com.ejc.util.TypeUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Config {

    @Setter
    private static Config instance;
    private YamlConfigFile defaultConfigFile;
    private YamlConfigFile configFileForProfile;

    public static synchronized Config getInstance() {
        if (instance == null) { // lazy instantiation for better testing
            instance = new Config();
            instance.init();
        }
        return instance;
    }

    private void init() {
        defaultConfigFile = YamlConfigFile.load("application.yml").orElse(null);
        if (!ActiveProfile.getCurrentProfile().equals("default")) {
            configFileForProfile = YamlConfigFile.load(String.format("application-%d.yml", ActiveProfile.getCurrentProfile())).orElse(null);
        }
    }


    public <T> T getProperty(String path, Class<T> type, String defaultValue, boolean mandatory) throws PropertyNotFoundException {
        T property = null;
        if (configFileForProfile != null) {
            property = configFileForProfile.findValue(path, type).orElse(null);
        }
        if (property == null && defaultConfigFile != null) {
            property = defaultConfigFile.findValue(path, type).orElse(null);
        }
        if (property == null && !defaultValue.equals("")) {
            property = convertDefaultValue(defaultValue, type, path);
        }
        if (mandatory && property == null) {
            throw new PropertyNotFoundException(path);
        }
        return property;

    }

    private <T> T convertDefaultValue(String defaultValue, Class<T> type, String name) {
        if (defaultValue.isEmpty()) {
            return null;
        }
        try {
            return TypeUtils.convertStringToSimple(defaultValue, type);
        } catch (IllegalArgumentException e) {
            throw new IllegalPropertyTypeException(name, defaultValue, type);
        }
    }

    public <T, C extends Collection<T>> C getCollectionProperty(String path, Class<C> collectionType, Class<T> elementType, String defaultValue, boolean mandatory) throws PropertyNotFoundException {
        C property = null;
        if (configFileForProfile != null) {
            property = configFileForProfile.findCollection(path, collectionType, elementType).orElse(null);
        }
        if (property == null) {
            property = defaultConfigFile.findCollection(path, collectionType, elementType).orElse(null);
        }
        if (!defaultValue.equals("")) {
            throw new IllegalStateException("'defaultValue' can not be used for collection-fields " + path);
        }
        if (property.isEmpty() && mandatory) {
            throw new PropertyNotFoundException(path);
        }
        C coll = TypeUtils.emptyCollection(collectionType);
        if (property != null) {
            coll.addAll(property);
        }
        return coll;
    }


    public <K, V, M extends Map<K, V>> M getMapProperty(String path, Class<? extends Map> mapType, Class<K> keyType, Class<V> valueType, String defaultValue, boolean mandatory) {
        M property = null;
        if (configFileForProfile != null) {
            property = (M) configFileForProfile.findMap(path, mapType, keyType, valueType).orElse(null);
        }
        if (property == null) {
            property = (M) defaultConfigFile.findMap(path, mapType, keyType, valueType).orElse(null);
        }
        if (!defaultValue.equals("")) {
            throw new IllegalStateException("'defaultValue' can not be used for collection-fields " + path);
        }
        if (property.isEmpty() && mandatory) {
            throw new PropertyNotFoundException(path);
        }
        M map = TypeUtils.emptyMap(mapType);
        if (property != null) {
            map.putAll(property);
        }
        return map;
    }


}