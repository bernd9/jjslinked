package com.ejc.api.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class ConfigTest {

    @Test
    void values() {
        assertThat(Config.getProperty("key1.key2", String.class, "", true)).isEqualTo("xx");
    }

    @Test
    void exceptionIfNotPresentAndMandatory() {
        assertThatThrownBy(() -> Config.getProperty("blabla", String.class, "", true)).isInstanceOf(PropertyNotFoundException.class);
    }

    @Test
    void noExceptionIfNotPresentAndNotMandatory() {
        assertThat(Config.getProperty("blabla", String.class, "", false)).isNull();
    }

}