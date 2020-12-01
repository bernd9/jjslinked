package com.ejc.processor.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConfigYmlToPropertiesConverterTest {

    private File yamlFile;
    private ConfigYmlToPropertiesConverter converter = new ConfigYmlToPropertiesConverter();

    @BeforeAll
    void initYamlFile() {
      yamlFile = new File(getClass().getClassLoader()
                .getResource("com/ejc/processor/config/test.yml").getFile());

      assertThat(yamlFile.exists()).isTrue();
    }

    @Test
    void test() throws IOException {
        converter.toProperties(yamlFile);
    }

}