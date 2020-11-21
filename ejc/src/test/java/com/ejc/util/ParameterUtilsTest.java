package com.ejc.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Parameter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParameterUtilsTest {

    private Parameter parameter1;
    private Parameter parameter2;

    @BeforeAll
    void initParameters() throws NoSuchMethodException {
        parameter1 = getClass().getDeclaredMethod("testMethod1", List.class).getParameters()[0];
        parameter2 = getClass().getDeclaredMethod("testMethod2", List.class).getParameters()[0];
    }

    void testMethod1(List<String> list) {
    }

    void testMethod2(List list) {
    }

    @Test
    void getGenericCollectionTypeEmpty() {
        assertThat(ParameterUtils.getGenericCollectionType(parameter2)).isEmpty();
    }


    @Test
    void getGenericCollectionTypePresent() {
        assertThat(ParameterUtils.getGenericCollectionType(parameter1)).contains(String.class);
    }
}