package com.ejc.processor;

import com.ejc.api.config.Config;
import com.ejc.api.context.ApplicationContext;
import com.ejc.util.FieldUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConfigTest {

    private ApplicationContext context;

    @BeforeAll
    void init() throws Exception {
        Config config = mock(Config.class);
        when(config.getProperty(eq("simpleString"), any(), any(), anyBoolean())).thenReturn("simpleString");
        when(config.getProperty(eq("simpleInt"), any(), any(), anyBoolean())).thenReturn(123);
        when(config.getCollectionProperty(eq("integerCollection"), any(), any(), any(), anyBoolean())).thenReturn(Set.of(1, 2, 3));
        when(config.getMapProperty(eq("map"), any(), any(), any(), eq(""), eq(true))).thenReturn(Map.of("x1", 1, "x2", 2));
        Config.setInstance(config);
        context = ProcessorTestUtil.compileContext("com.ejc.processor.config.TestApp");
    }

    @Test
    void simpleStringField() {
        Object singleton1 = ProcessorTestUtil.getSingletonBySimpleClassName("Singleton1", context);
        assertThat(FieldUtils.getFieldValue(singleton1, "simpleString")).isEqualTo("simpleString");
    }

    @Test
    void simpleIntField() {
        Object singleton1 = ProcessorTestUtil.getSingletonBySimpleClassName("Singleton1", context);
        assertThat(FieldUtils.getFieldValue(singleton1, "simpleInt")).isEqualTo(123);
    }

    @Test
    void collectionField() {
        Object singleton1 = ProcessorTestUtil.getSingletonBySimpleClassName("Singleton1", context);
        assertThat((Collection<Integer>) FieldUtils.getFieldValue(singleton1, "integerCollection")).contains(1, 2, 3);
    }

    @Test
    void mapField() {
        Object singleton1 = ProcessorTestUtil.getSingletonBySimpleClassName("Singleton1", context);
        assertThat((Map<String, Integer>) FieldUtils.getFieldValue(singleton1, "map")).containsAllEntriesOf(Map.of("x1", 1, "x2", 2));
    }

    @Test
    void simpleStringParameter() {
        Object singleton2 = ProcessorTestUtil.getSingletonBySimpleClassName("Singleton1", context);
        assertThat(FieldUtils.getFieldValue(singleton2, "simpleString")).isEqualTo("simpleString");
    }

    @Test
    void simpleIntParameter() {
        Object singleton2 = ProcessorTestUtil.getSingletonBySimpleClassName("Singleton1", context);
        assertThat(FieldUtils.getFieldValue(singleton2, "simpleInt")).isEqualTo(123);
    }

    @Test
    void collectionParameter() {
        Object singleton2 = ProcessorTestUtil.getSingletonBySimpleClassName("Singleton1", context);
        assertThat((Collection<Integer>) FieldUtils.getFieldValue(singleton2, "integerCollection")).contains(1, 2, 3);
    }

    @Test
    void mapParameter() {
        Object singleton2 = ProcessorTestUtil.getSingletonBySimpleClassName("Singleton1", context);
        assertThat((Map<String, Integer>) FieldUtils.getFieldValue(singleton2, "map")).containsAllEntriesOf(Map.of("x1", 1, "x2", 2));
    }

}
