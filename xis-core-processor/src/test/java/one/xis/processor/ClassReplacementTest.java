package one.xis.processor;

import com.ejc.api.context.ApplicationContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClassReplacementTest {

    private ApplicationContext context;

    @BeforeAll
    void init() throws Exception {
        context = ProcessorTestUtil.compileContext("one.xis.processor.classReplacement.TestApp");
    }

    @Test
    void replaceBySingletonAnnotation() {
        assertThat(ProcessorTestUtil.getOptionalSingletonBySimpleClassName("Singleton1", context)).isEmpty();
        assertThat(ProcessorTestUtil.getOptionalSingletonBySimpleClassName("Singleton2", context)).isEmpty();
        assertThat(ProcessorTestUtil.getOptionalSingletonBySimpleClassName("Singleton3", context)).isPresent();
    }

    @Test
    void implementation() {
        assertThat(ProcessorTestUtil.getOptionalSingletonBySimpleClassName("Implementation1", context)).isPresent();
    }


}