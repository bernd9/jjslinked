package one.xis.sql.processor;

import com.google.testing.compile.*;
import com.google.testing.compile.Compiler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import static com.google.testing.compile.Compiler.javac;

class EntityAnnotationProcessorTest {

    private Compiler compiler;
    private JavaFileObject entity;
    private Compilation compilation;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new EntityAnnotationProcessor());
        entity = JavaFileObjects.forResource("one/xis/sql/processor/Customer.java");

        compilation = compiler.compile(entity);
        //assertThat(compilation.status()).isEqualTo(Compilation.Status.SUCCESS);
    }

    @Test
    void entityStatements() {
        CompilationSubject.assertThat(compilation).generatedSourceFile("one.xis.sql.processor.CustomerStatements")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("one/xis/sql/processor/CustomerStatements.java"));
    }

    @Test
    void entityResultSets() {
        CompilationSubject.assertThat(compilation).generatedSourceFile("one.xis.sql.processor.CustomerResultSet")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("one/xis/sql/processor/CustomerResultSet.java"));
    }

    public String getSource(JavaFileObject fileObject) {
        StringWriter writer = new StringWriter();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(fileObject.openInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                writer.write(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String src = writer.toString();
        return src;
    }
}