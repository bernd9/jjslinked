package com.ejc.processor;

import com.ejc.api.context.ApplicationContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConfigValueTest {
    private ApplicationContext context;

    @BeforeAll
    void init() throws Exception {
        context = ProcessorTestUtil.compileContext("com.ejc.processor.configValues.TestApp");
    }

    @Test
    void fieldInjection() {
        Object singleton1 = ProcessorTestUtil.getSingletonBySimpleClassName("Singleton1", context);
        assertThat(singleton1).isNotNull();
        //assertThat(FieldUtils.getFieldValue(singleton1, "dependency").getClass().getSimpleName()).isEqualTo("Singleton2");
    }
}
