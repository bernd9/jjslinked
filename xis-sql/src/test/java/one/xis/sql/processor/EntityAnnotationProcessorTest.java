package one.xis.sql.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityAnnotationProcessorTest {

    private Compiler compiler;
    private JavaFileObject entity;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new EntityAnnotationProcessor());
        entity = JavaFileObjects.forResource("one/xis/sql/processor/Customer.java");
    }

    @Test
    void test() {
        Compilation compilation = compiler.compile(entity);
        assertThat(compilation.status()).isEqualTo(Compilation.Status.SUCCESS);
        assertEquals(1, compilation.generatedSourceFiles().size());
    }

}