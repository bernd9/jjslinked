package com.ejc.processor;

import com.ejc.ApplicationContext;
import com.ejc.api.context.ClassReference;
import com.ejc.util.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BeanMethodTest {

    @BeforeEach
    void init() {
        ClassReference.flush();
    }

    @Test
    void test() throws Exception {
        ApplicationContext context = ProcessorTestUtil.compileContext("com.ejc.processor.BeanMethodTestApp");

        Object bean1 = ProcessorTestUtil.getSingletonBySimpleClassName("Bean1", context);

        assertThat(FieldUtils.getFieldValue(bean1, "dependency1").getClass().getSimpleName()).isEqualTo("Singleton2");
        assertThat(FieldUtils.getFieldValue(bean1, "dependency2").getClass().getSimpleName()).isEqualTo("Singleton3");
        assertThat(FieldUtils.getFieldValue(bean1, "dependency3").getClass().getSimpleName()).isEqualTo("Singleton4");

    }


}