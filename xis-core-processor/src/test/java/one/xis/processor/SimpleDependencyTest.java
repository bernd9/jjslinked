package one.xis.processor;

import one.xis.context.ApplicationContext;
import one.xis.util.FieldUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SimpleDependencyTest {

    private ApplicationContext context;

    @BeforeAll
    void init() throws Exception {
        context = ProcessorTestUtil.compileContext("one.xis.processor.simpleDependency.TestApp");
    }

    @Test
    void fieldInjection() {
        Object singleton1 = ProcessorTestUtil.getSingletonBySimpleClassName("Singleton1", context);
        assertThat(singleton1).isNotNull();
        assertThat(FieldUtils.getFieldValue(singleton1, "dependency").getClass().getSimpleName()).isEqualTo("Singleton2");
    }

    @Test
    void constructorInjection() {
        Object singleton2 = ProcessorTestUtil.getSingletonBySimpleClassName("Singleton2", context);
        assertThat(singleton2).isNotNull();
        assertThat(FieldUtils.getFieldValue(singleton2, "dependency").getClass().getSimpleName()).isEqualTo("Singleton3");
    }


}