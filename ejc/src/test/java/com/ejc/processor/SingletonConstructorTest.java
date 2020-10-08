package com.ejc.processor;

import com.ejc.api.context.ApplicationContextInitializer;
import com.ejc.api.context.ClassReference;
import com.ejc.util.CollectorUtils;
import com.ejc.util.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class SingletonConstructorTest {

    @BeforeEach
    void init() {
        ClassReference.flush();
    }


    @Test
    void test() throws Exception {
        ApplicationContextInitializer initializer = ProcessorTestUtil.compileContext("com.ejc.processor.singletonconstr.SingletonConstructorTestApp");

        Object singleton1 = initializer.getSingletons().stream().filter(o -> o.getClass().getSimpleName().equals("Singleton1")).collect(CollectorUtils.toOnlyElement());
        
        assertThat(FieldUtils.getFieldValue(singleton1, "dependency1").getClass().getSimpleName()).isEqualTo("Singleton2");
        assertThat(FieldUtils.getFieldValue(singleton1, "dependency2").getClass().getSimpleName()).isEqualTo("Singleton3");

    }


}