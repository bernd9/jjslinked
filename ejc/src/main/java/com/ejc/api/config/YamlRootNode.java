package com.ejc.api.config;

import java.util.Iterator;

class YamlRootNode {

    private YamlNode firstChild;

    void addNode(YamlNode yamlNode) {
        if (firstChild != null) {
            YamlNode lastChild = firstChild.lastSibling();
            lastChild.addNode(yamlNode);
        } else {
            firstChild = yamlNode;
        }
    }

    YamlNode findNode(Iterator<String> path) {
        if (firstChild == null || !path.hasNext()) {
            return null;
        }
        return firstChild.findNode(path);
    }


}
