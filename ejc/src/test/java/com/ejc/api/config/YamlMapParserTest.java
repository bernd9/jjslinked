package com.ejc.api.config;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class YamlMapParserTest {

    private YamlMapParser parser = new YamlMapParser();

    @Test
    void testPattern() {
        assertThat(YamlMapParser.KEY_VALUE_PATTERN.matcher("x1: a").find()).isTrue();
    }

    @Test
    void parseStringKeyValue() {
        String s = "x1: a, x2: b";
        Map<String, String> result = parser.parseMap(s, String.class, String.class);
        assertThat(result.get("x1")).isEqualTo("a");
        assertThat(result.get("x2")).isEqualTo("b");
    }
}