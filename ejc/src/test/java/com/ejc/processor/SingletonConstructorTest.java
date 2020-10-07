package com.ejc.processor;

import com.ejc.api.context.ApplicationContextInitializer;
import com.ejc.util.CollectorUtils;
import com.ejc.util.FieldUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SingletonConstructorTest {
    
    @Test
    void test() throws Exception {
        ApplicationContextInitializer initializer = ProcessorTestUtil.compileContext("com.ejc.processor.SingletonConstructorTestApp");

        Object singleton1 = initializer.getSingletons().stream().filter(o -> o.getClass().getSimpleName().equals("Singleton1")).collect(CollectorUtils.toOnlyElement());

        assertThat(initializer.getSingletons()).hasSize(3);
        assertThat(FieldUtils.getFieldValue(singleton1, "dependency1").getClass().getSimpleName()).isEqualTo("Singleton2");
        assertThat(FieldUtils.getFieldValue(singleton1, "dependency2").getClass().getSimpleName()).isEqualTo("Singleton3");

    }


}