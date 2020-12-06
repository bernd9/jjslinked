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

    private static YamlConfiguration instance;

    void init() {
        defaultConfigRoot = yamlParser.parse(yamlLines("application.yml"));
        if (profile.equals("default")) {
            configRootForProfile = Optional.empty();
        } else {
            configRootForProfile = Optional.of(yamlParser.parse(yamlLines(String.format("application-%d.yml", profile))));
        }
    }

    <T> Optional<T> findSingleValue(List<String> path, Class<T> targetType) {
        return findKeyValueNode(path)
                .filter(Objects::nonNull)
                .map(YamlNode::getValue)
                .map(str -> TypeUtils.convertStringToSimple(str, targetType));
    }

    <T, C extends Collection> C findCollectionValue(List<String> path, Class<C> collectionType, Class<T> elementType) {
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

    private Optional<YamlNode> findKeyValueNode(List<String> path) {
        Optional<YamlNode> yamlNode = findNode(path);
        yamlNode.ifPresent(this::verifyIsKeyValue);
        return yamlNode;
    }

    private Optional<YamlNode> findKeyNode(List<String> path) {
        Optional<YamlNode> yamlNode = findNode(path);
        yamlNode.ifPresent(this::verifyIsKey);
        return yamlNode;
    }

    private void verifyIsKeyValue(YamlNode yamlNode) {
        if (yamlNode.getNodeType() != YamlNodeType.KEY_VALUE) {
            throw new IllegalStateException("value found but not a key-value-node");
        }
    }

    private void verifyIsKey(YamlNode yamlNode) {
        if (yamlNode.getNodeType() != YamlNodeType.KEY) {
            throw new IllegalStateException("value found but not a key-node");
        }
    }

    private void verifyIsArrayElement(YamlNode yamlNode) {
        if (yamlNode.getNodeType() != YamlNodeType.ARRAY_ELEMENT) {
            throw new IllegalStateException("value found but not a array-element-node");
        }
    }

    private Optional<YamlNode> findNode(List<String> path) {
        Optional<YamlNode> yamlNode = findInProfileConfig(path);
        if (yamlNode.isPresent()) {
            return yamlNode;
        }
        return findInDefaultConfig(path);
    }

    private Optional<YamlNode> findInProfileConfig(List<String> path) {
        return configRootForProfile.map(node -> node.findNode(path.iterator())).filter(Objects::nonNull);
    }

    private Optional<YamlNode> findInDefaultConfig(List<String> path) {
        return Optional.ofNullable(defaultConfigRoot.findNode(path.iterator()));
    }

    private List<String> yamlLines(String resourceName) {
        try {
            return IOUtils.lines(resourceName, "UTF-8", Thread.currentThread().getContextClassLoader());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}
