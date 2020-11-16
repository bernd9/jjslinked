package com.ejc.api.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfigTest {

    @Test
    void values() {
        System.setProperty("profile", "test");

        assertThat(Config.getProperty("string", String.class, "", true)).isEqualTo("value1");
        assertThat(Config.getProperty("integer", Integer.class, "", true)).isEqualTo(123);
        assertThat(Config.getProperty("float", Float.class, "", true)).isEqualTo(1.2f);
        assertThat(Config.getProperty("char", Character.class, "", true)).isEqualTo('c');
        assertThat(Config.getProperty("value1.sub1", String.class, "", true)).isEqualTo("sub1");
        assertThat(Config.getProperty("value1.sub2", String.class, "", true)).isEqualTo("replaced");
    }

    @Test
    void exceptionIfNotPresentAndMandatory() {
        assertThatThrownBy(() -> Config.getProperty("blabla", String.class, "", true)).isInstanceOf(PropertyNotFoundException.class);
    }

    @Test
    void noExceptionIfNotPresentAndNotMandatory() {
        assertThat(Config.getProperty("blabla", String.class, "", false)).isEmpty();
    }

}