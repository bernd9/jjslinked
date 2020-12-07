package com.ejc.api.config;

import com.ejc.util.IOUtils;
import com.ejc.util.TypeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class YamlConfiguration {
    private final String profile;
    private YamlRootNode defaultConfigRoot;
    private Optional<YamlRootNode> configRootForProfile;
    private final YamlParser yamlParser = new YamlParser();

    private static final YamlMapParser MAP_PARSER = new YamlMapParser();

    void init() {
        defaultConfigRoot = yamlParser.parse(yamlLines("application.yml"));
        if (profile.equals("default")) {
            configRootForProfile = Optional.empty();
        } else {
            configRootForProfile = Optional.of(yamlParser.parse(yamlLines(String.format("application-%d.yml", profile))));
        }
    }

    <T> Optional<T> findSingleValue(String path, Class<T> targetType) {
        return findKeyValueNode(path)
                .filter(Objects::nonNull)
                .map(YamlNode::getValue)
                .map(str -> TypeUtils.convertStringToSimple(str, targetType));
    }

    <T, C extends Collection> C findCollectionValue(String path, Class<C> collectionType, Class<T> elementType) {
        Collection<T> collection = TypeUtils.emptyCollection(collectionType);
        collection.addAll(findKeyNode(path)
                .filter(Objects::nonNull)
                .map(YamlNode::getChildNodes)
                .orElse(Collections.emptyList())
                .stream()
                .peek(this::verifyIsArrayElement)
                .map(YamlNode::getValue)
                .map(e -> TypeUtils.convertStringToSimple(e, elementType))
                .collect(Collectors.toList()));
        return (C) collection;
    }

    <K, V> Optional<Map<K, V>> findMapValue(String path, Class<K> keyType, Class<V> valueType) {
        return findMapNode(path)
                .filter(Objects::nonNull)
                .map(YamlNode::getValue)
                .map(str -> MAP_PARSER.parseMap(str, keyType, valueType));
    }

    private Optional<YamlNode> findKeyValueNode(String path) {
        Optional<YamlNode> yamlNode = findNode(path);
        yamlNode.ifPresent(this::verifyIsKeyValue);
        return yamlNode;
    }

    private Optional<YamlNode> findMapNode(String path) {
        Optional<YamlNode> yamlNode = findNode(path);
        yamlNode.ifPresent(this::verifyIsMapValue);
        return yamlNode;
    }

    private Optional<YamlNode> findKeyNode(String path) {
        Optional<YamlNode> yamlNode = findNode(path);
        yamlNode.ifPresent(this::verifyIsKey);
        return yamlNode;
    }

    private void verifyIsKeyValue(YamlNode yamlNode) {
        if (yamlNode.getNodeType() != YamlNodeType.KEY_VALUE) {
            throw new IllegalStateException("value found but not a key-value-node" + yamlNode.getLine());
        }
    }

    private void verifyIsKey(YamlNode yamlNode) {
        if (yamlNode.getNodeType() != YamlNodeType.KEY) {
            throw new IllegalStateException("value found but not a key-node" + yamlNode.getLine());
        }
    }


    private void verifyIsMapValue(YamlNode yamlNode) {
        if (yamlNode.getNodeType() != YamlNodeType.MAP) {
            throw new IllegalStateException("value found but not a map-node: " + yamlNode.getLine());
        }
    }


    private void verifyIsArrayElement(YamlNode yamlNode) {
        if (yamlNode.getNodeType() != YamlNodeType.ARRAY_ELEMENT) {
            throw new IllegalStateException("value found but not a array-element-node");
        }
    }

    private Optional<YamlNode> findNode(String path) {
        Optional<YamlNode> yamlNode = findInProfileConfig(new ConfigPath(path));
        if (yamlNode.isPresent()) {
            return yamlNode;
        }
        return findInDefaultConfig(new ConfigPath(path));
    }

    private Optional<YamlNode> findInProfileConfig(ConfigPath path) {
        return configRootForProfile.map(node -> node.findNode(path)).filter(Objects::nonNull);
    }

    private Optional<YamlNode> findInDefaultConfig(ConfigPath path) {
        return Optional.ofNullable(defaultConfigRoot.findNode(path));
    }

    private List<String> yamlLines(String resourceName) {
        try {
            return IOUtils.lines(resourceName, "UTF-8", Thread.currentThread().getContextClassLoader());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

}
