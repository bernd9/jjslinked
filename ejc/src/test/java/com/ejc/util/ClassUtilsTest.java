package com.ejc.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClassUtilsTest {

    @Test
    void getPackageName() {
        assertThat(com.ejc.util.ClassUtils.getPackageName("com.xyz.Test").get()).isEqualTo("com.xyz");
        assertThat(com.ejc.util.ClassUtils.getPackageName("Test")).isEmpty();
    }

    @Test
    void getSimpleName() {
        assertThat(com.ejc.util.ClassUtils.getSimpleName("com.xyz.Test")).isEqualTo("Test");
        assertThat(ClassUtils.getSimpleName("Test")).isEqualTo("Test");
    }
}