package one.xis.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({"unchecked", "raw"})
class YamlConfigFileTest {

    private YamlConfigFile yamlConfigFile = new YamlConfigFile();

    @BeforeEach
    void setUp() {
        yamlConfigFile = YamlConfigFile.load("application.yml").orElseThrow();
    }

    @Test
    void findValue() {
        Optional<String> value = yamlConfigFile.findValue("key1.key2", String.class);
        assertThat(value).isPresent();
        assertThat(value.get()).isEqualTo("xx");
    }

    @Test
    void findList() {
        Optional<List> value = yamlConfigFile.findCollection("key1.list", List.class, Integer.class);
        assertThat(value).isPresent();
        assertThat(value.get()).containsExactly(1, 2, 3);
    }

    @Test
    void findMap() {
        Optional<Map> value = yamlConfigFile.findMap("key1.map", Map.class, String.class, Integer.class);
        assertThat(value).isPresent();
        assertThat(value.get().get("a")).isEqualTo(1);
        assertThat(value.get().get("b")).isEqualTo(2);

    }
}

