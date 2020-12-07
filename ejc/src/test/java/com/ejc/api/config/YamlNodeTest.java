package com.ejc.api.config;

import org.junit.jupiter.api.Test;

class YamlNodeTest {

    @Test
    void mapPattern() {
        boolean b = YamlNode.MAP_PATTERN.matcher("map: { x: 1, y: 2 }").find();
    }

}