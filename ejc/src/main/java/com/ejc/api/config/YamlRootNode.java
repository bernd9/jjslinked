package com.ejc.api.config;

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

    YamlNode findNode(ConfigPath path) {
        if (firstChild == null || !path.hasNext()) {
            return null;
        }
        return firstChild.findNode(path);
    }


}
