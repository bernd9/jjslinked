package com.ejc.processor.config;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigYmlFinderTest {
    private ConfigYmlFinder ymlFinder = new ConfigYmlFinder();

    @Test
    void getConfigFilesByProfile() {
        Map<String, File> configMap = ymlFinder.getConfigFilesByProfile();
        assertThat(configMap).hasSize(3);
        assertThat(configMap.get("default")).hasName("application.yml");
        assertThat(configMap.get("x")).hasName("application-x.yml");
        assertThat(configMap.get("y")).hasName("application-y.yml");
    }
}