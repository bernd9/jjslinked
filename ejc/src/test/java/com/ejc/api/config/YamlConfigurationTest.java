package com.ejc.api.config;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class YamlConfigurationTest {

    @Test
    void parseAndFindStringLevel2() {
        YamlConfiguration configuration = new YamlConfiguration("default");
        configuration.init();
        Optional<String> value = configuration.findSingleValue(List.of("key1", "key2"), String.class);
        assertThat(value).contains("xx");
    }


    @Test
    void parseAndFindListLevel2() {
        YamlConfiguration configuration = new YamlConfiguration("default");
        configuration.init();
        List<Integer> value = configuration.findCollectionValue(List.of("key1", "list"), List.class, Integer.class);
        assertThat(value).containsExactly(1, 2, 3);
    }
}