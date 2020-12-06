package com.ejc.api.config;

import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
class YamlParser {

    YamlRootNode parse(List<String> yamlLines) {
        Iterator<YamlNode> yamlNodes = yamlLines.stream()
                .map(YamlNode::new)
                .iterator();
        YamlRootNode root = new YamlRootNode();
        while (yamlNodes.hasNext()) {
            root.addNode(yamlNodes.next());
        }
        return root;
    }

}