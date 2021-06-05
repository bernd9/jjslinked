package one.xis.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AdviceClassAnnotationProcessorTest {

    private Compiler compiler;
    private JavaFileObject bean;
    private JavaFileObject annotation;
    private JavaFileObject handler;


    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new AdviceClassAnnotationProcessor());
        bean = JavaFileObjects.forResource("one/xis/processor/advice/AdviceTestBean.java");
        annotation = JavaFileObjects.forResource("one/xis/processor/advice/TestAnnotation.java");
        handler = JavaFileObjects.forResource("one/xis/processor/advice/TestHandler.java");
    }

    @Test
    void test() {
        Compilation compilation = compiler.compile(bean, annotation, handler);
        ProcessorTestUtil.assertSuccess(compilation);
        assertEquals(1, compilation.generatedSourceFiles().size());
    }


}