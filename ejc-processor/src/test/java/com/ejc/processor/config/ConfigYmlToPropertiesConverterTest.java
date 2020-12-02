package com.ejc.processor.config;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;


class ConfigYmlToPropertiesConverterTest {
    private ConfigYmlToPropertiesConverter converter = new ConfigYmlToPropertiesConverter();

    @Nested
    class SimpleYmlTest {

        private File yamlFile;

        @BeforeEach
        void init() {
            yamlFile = yamlTestFile("simple.yml");
        }

        @Test
        void test() throws IOException {
            Properties properties = converter.toProperties(yamlFile);
            assertThat(properties.get("key1")).isEqualTo("value1");
            assertThat(properties.get("key2")).isEqualTo("value2");
            assertThat(properties.get("key3")).isEqualTo("");
            assertThat(properties.get("key4")).isEqualTo("");
        }

    }

    @Nested
    class SimpleYml2LevelsTest {

        private File yamlFile;

        @BeforeEach
        void init() {
            yamlFile = yamlTestFile("simple2Levels.yml");
        }

        @Test
        void test() throws IOException {
            Properties properties = converter.toProperties(yamlFile);
            assertThat(properties.get("key1.key1a")).isEqualTo("1");
            assertThat(properties.get("key1.key1b")).isEqualTo("2");
            assertThat(properties.get("key2.key2a")).isEqualTo("a");
            assertThat(properties.get("key2.key2b")).isEqualTo("b");
        }

    }


    @Nested
    class ListYmlTest {

        private File yamlFile;

        @BeforeEach
        void init() {
            yamlFile = yamlTestFile("list.yml");
        }

        @Test
        void test() throws IOException {
            Properties properties = converter.toProperties(yamlFile);
            assertThat(properties.get("key[0]")).isEqualTo("a");
            assertThat(properties.get("key[1]")).isEqualTo("b");
            assertThat(properties.get("key[2]")).isEqualTo("c");
        }

    }

    @Nested
    class SimpleAndListYmlTest {

        private File yamlFile;

        @BeforeEach
        void init() {
            yamlFile = yamlTestFile("simpleAndList.yml");
        }

        @Test
        void test() throws IOException {
            Properties properties = converter.toProperties(yamlFile);
            assertThat(properties.get("simple")).isEqualTo("simplevalue");
            assertThat(properties.get("list[0]")).isEqualTo("1");
            assertThat(properties.get("list[1]")).isEqualTo("2");
            assertThat(properties.get("list[2]")).isEqualTo("3");
            assertThat(properties.get("list[3]")).isEqualTo("");
        }

    }


    @Nested
    class SimpleAndList2LevelsYmlTest {

        private File yamlFile;

        @BeforeEach
        void init() {
            yamlFile = yamlTestFile("simpleAndList2Levels.yml");
        }

        @Test
        void test() throws IOException {
            Properties properties = converter.toProperties(yamlFile);
            assertThat(properties.get("key.list[0]")).isEqualTo("a");
            assertThat(properties.get("key.list[1]")).isEqualTo("b");
            assertThat(properties.get("key.list[2]")).isEqualTo("c");
            assertThat(properties.get("key.simple")).isEqualTo("x");
        }

    }

    private File yamlTestFile(String name) {
        return new File(ConfigYmlToPropertiesConverterTest.class.getClassLoader()
                .getResource("com/ejc/processor/config/" + name).getFile());
    }
}