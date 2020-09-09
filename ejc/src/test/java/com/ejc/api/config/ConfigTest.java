package com.ejc.api.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigTest {


    @Test
    void values() {
        System.setProperty("profile", "test");

        assertThat(Config.getProperty("string", String.class)).isEqualTo("value1");
        assertThat(Config.getProperty("integer", Integer.class)).isEqualTo(123);
        assertThat(Config.getProperty("float", Float.class)).isEqualTo(1.2f);
        assertThat(Config.getProperty("char", Character.class)).isEqualTo('c');
        assertThat(Config.getProperty("value1.sub1", String.class)).isEqualTo("sub1");
        assertThat(Config.getProperty("value1.sub2", String.class)).isEqualTo("replaced");
    }

}