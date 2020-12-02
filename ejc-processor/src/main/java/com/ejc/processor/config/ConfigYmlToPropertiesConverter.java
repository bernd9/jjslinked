package com.ejc.processor.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

class ConfigYmlToPropertiesConverter {
private ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    Properties toProperties(File yamlFile) throws IOException {
        Properties properties = new Properties();
        JsonNode jsonNode = objectMapper.readTree(yamlFile);
        evaluateChildren(jsonNode, new ArrayList<>(), properties);
        return properties;
    }

    private void evaluateChildren(JsonNode jsonNode, List<String> propertyPath, Properties properties) {
        Iterator<Map.Entry<String, JsonNode>> nodeIterator = jsonNode.fields();
        while (nodeIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = nodeIterator.next();
            JsonNode childNode = entry.getValue();
            evaluate(entry.getKey(), childNode, propertyPath, properties);
        }
    }

    private void evaluate(String name, JsonNode childNode, List<String> propertyPath, Properties properties) {
        if (childNode.isValueNode()) {
            List<String> childPropertyPath = new ArrayList<>(propertyPath);
            childPropertyPath.add(name);
            evaluateValueNode((ValueNode) childNode, childPropertyPath, properties);
        } else if (childNode.isObject()){
            List<String> childPropertyPath = new ArrayList<>(propertyPath);
            childPropertyPath.add(name);
            evaluateObjectNode((ObjectNode) childNode, childPropertyPath, properties);
        } else if (childNode.isArray()) {
            evaluateArrayNode(name, (ArrayNode) childNode, propertyPath, properties);
        }
    }

    private void evaluateObjectNode(ObjectNode node, List<String> propertyPath, Properties properties) {
        evaluateChildren(node, propertyPath, properties);
    }

    private void evaluateValueNode(ValueNode node, List<String> propertyPath, Properties properties) {
        if (node.isNull()) {
            properties.put(name(propertyPath), "");
        } else {
            properties.put(name(propertyPath), node.asText());
        }

    }

    private void evaluateArrayNode(String name, ArrayNode arrayNode, List<String> propertyPath, Properties properties) {
        for (int i = 0; i < arrayNode.size(); i++) {
            JsonNode childNode  = arrayNode.get(i);
            List<String> childPropertyPath = new ArrayList<>(propertyPath);
            String key = String.format("%s[%d]", name, i);
            childPropertyPath.add(key);
            if (childNode.isValueNode()) {
                evaluateValueNode((ValueNode) childNode, childPropertyPath, properties);
            } else if (childNode.isObject()){
                evaluateObjectNode((ObjectNode) childNode, childPropertyPath, properties);
            } else if (childNode.isArray()) {
                evaluateArrayNode(name, (ArrayNode) childNode, childPropertyPath, properties);
            }
        }
    }

    private String name(List<String> propertyPath) {
        return propertyPath.stream().collect(Collectors.joining("."));
    }

}
