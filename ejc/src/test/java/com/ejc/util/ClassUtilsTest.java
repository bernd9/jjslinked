package com.ejc.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClassUtilsTest {

    @Test
    void getPackageName() {
        assertThat(ClassUtils.getPackageName("com.xyz.Test").get()).isEqualTo("com.xyz");
        assertThat(ClassUtils.getPackageName("Test")).isEmpty();
    }

    @Test
    void getSimpleName() {
        assertThat(ClassUtils.getSimpleName("com.xyz.Test")).isEqualTo("Test");
        assertThat(ClassUtils.getSimpleName("Test")).isEqualTo("Test");
    }
}