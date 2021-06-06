package one.xis.processor;

import one.xis.context.ApplicationContext;
import com.ejc.util.FieldUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BeanMethodTest {

    private ApplicationContext context;

    @BeforeAll
    void init() throws Exception {
        context = ProcessorTestUtil.compileContext("one.xis.processor.beanMethod.TestApp");
    }

    @Test
    void config1() {
        Object config1 = ProcessorTestUtil.getSingletonBySimpleClassName("Config1", context);
        assertThat(config1).isNotNull();
        assertThat(FieldUtils.getFieldValue(config1, "dependency1").getClass().getSimpleName()).isEqualTo("Singleton1");
        assertThat(FieldUtils.getFieldValue(config1, "dependency2").getClass().getSimpleName()).isEqualTo("Singleton2");
        assertThat(Arrays.stream(FieldUtils.getFieldValue(config1, "dependency2").getClass().getInterfaces()).map(Class::getSimpleName)).containsExactly("Interface1");
    }

    @Test
    void singleton1() {
        Object singleton2 = ProcessorTestUtil.getSingletonBySimpleClassName("Singleton1", context);
        assertThat(singleton2).isNotNull();
    }

    @Test
    void singleton2() {
        Object singleton2 = ProcessorTestUtil.getSingletonBySimpleClassName("Singleton2", context);
        assertThat(singleton2).isNotNull();
    }

    @Test
    void singleton3() {
        Object singleton3 = ProcessorTestUtil.getSingletonBySimpleClassName("Singleton3", context);
        assertThat(singleton3).isNotNull();
    }

    @Test
    void bean1() {
        Object bean1 = ProcessorTestUtil.getSingletonBySimpleClassName("Bean1", context);
        assertThat(bean1).isNotNull();
        assertThat(FieldUtils.getFieldValue(bean1, "dependency1").getClass().getSimpleName()).isEqualTo("Singleton1");
        assertThat(FieldUtils.getFieldValue(bean1, "dependency2").getClass().getSimpleName()).isEqualTo("Singleton2");
        assertThat(FieldUtils.getFieldValue(bean1, "dependency3").getClass().getSimpleName()).isEqualTo("Singleton3");
    }


}