package com.ejc.processor;

import com.ejc.ApplicationContext;
import com.ejc.util.FieldUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CollectionDependencyTest {

    private ApplicationContext context;

    @BeforeAll
    void init() throws Exception {
        context = ProcessorTestUtil.compileContext("com.ejc.processor.collectionDependency.TestApp");
    }

    @Test
    void fieldInjection() {
        Object configuration1 = ProcessorTestUtil.getSingletonBySimpleClassName("Configuration1", context);
        assertThat(configuration1).isNotNull();
        Set<Object> fieldValues = FieldUtils.getFieldValue(configuration1, "dependency", Set.class);
        Set<String> fieldValueTypes = fieldValues.stream()
                .map(Object::getClass)
                .map(Class::getSimpleName)
                .collect(Collectors.toSet());
        assertThat(fieldValueTypes).containsAll(Set.of("Singleton2", "Singleton3"));
    }

    @Test
    void constructorInjection() {
        Object configuration2 = ProcessorTestUtil.getSingletonBySimpleClassName("Configuration2", context);
        assertThat(configuration2).isNotNull();
        Set<Object> fieldValues = FieldUtils.getFieldValue(configuration2, "dependency", Set.class);
        Set<String> fieldValueTypes = fieldValues.stream()
                .map(Object::getClass)
                .map(Class::getSimpleName)
                .collect(Collectors.toSet());
        assertThat(fieldValueTypes).containsAll(Set.of("Singleton2", "Singleton3"));
    }


}