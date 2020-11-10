package com.ejc.processor;

import com.ejc.ApplicationContext;
import com.ejc.api.context.ClassReference;
import com.ejc.util.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class BeanMethodTest {

    @BeforeEach
    void init() {
        ClassReference.flush();
    }

    @Test
    void test() throws Exception {
        ApplicationContext context = ProcessorTestUtil.compileContext("com.ejc.processor.BeanMethodTestApp");

        Object config1 = ProcessorTestUtil.getSingletonBySimpleClassName("Config1", context);
        assertThat(config1).isNotNull();
        assertThat(FieldUtils.getFieldValue(config1, "dependency1").getClass().getSimpleName()).isEqualTo("Singleton2");
        assertThat(FieldUtils.getFieldValue(config1, "dependency2").getClass().getSimpleName()).isEqualTo("Singleton3");
        assertThat(Arrays.stream(FieldUtils.getFieldValue(config1, "dependency2").getClass().getInterfaces()).map(Class::getSimpleName)).containsExactly("Interface1");

        Object singleton2 = ProcessorTestUtil.getSingletonBySimpleClassName("Singleton2", context);
        assertThat(singleton2).isNotNull();

        Object singleton3 = ProcessorTestUtil.getSingletonBySimpleClassName("Singleton3", context);
        assertThat(singleton3).isNotNull();

        Object bean1 = ProcessorTestUtil.getSingletonBySimpleClassName("Bean1", context);
        assertThat(bean1).isNotNull();
        assertThat(FieldUtils.getFieldValue(bean1, "dependency1").getClass().getSimpleName()).isEqualTo("Singleton2");
        assertThat(FieldUtils.getFieldValue(bean1, "dependency2").getClass().getSimpleName()).isEqualTo("Singleton3");
        assertThat(FieldUtils.getFieldValue(bean1, "dependency3").getClass().getSimpleName()).isEqualTo("Singleton4");

        Object singleton4 = ProcessorTestUtil.getSingletonBySimpleClassName("Singleton4", context);
        assertThat(singleton4).isNotNull();
    }


}