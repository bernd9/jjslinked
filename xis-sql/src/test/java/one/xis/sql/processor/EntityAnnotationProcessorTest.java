package one.xis.sql.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
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
    private JavaFileObject customer;
    private JavaFileObject invoiceAddress;
    private JavaFileObject order;
    private JavaFileObject agent;
    private JavaFileObject customerRepository;
    private Compilation compilation;

    @BeforeEach
    void init() {
        compiler = javac().withProcessors(new EntityAnnotationProcessor());
        customer = JavaFileObjects.forResource("one/xis/sql/processor/Customer.java");
        invoiceAddress = JavaFileObjects.forResource("one/xis/sql/processor/Address.java");
        order = JavaFileObjects.forResource("one/xis/sql/processor/Order.java");
        agent = JavaFileObjects.forResource("one/xis/sql/processor/Agent.java");
        customerRepository = JavaFileObjects.forResource("one/xis/sql/processor/CustomerRepository.java");
        compilation = compiler.compile(customer, invoiceAddress, order, agent, customerRepository);
        //assertThat(compilation.status()).isEqualTo(Compilation.Status.SUCCESS);
    }

    // TODO test for CrudHandler and CrossTableAccessor
    @Test
    void customerUtil() {
        CompilationSubject.assertThat(compilation).generatedSourceFile("one.xis.sql.processor.CustomerUtil")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("one/xis/sql/processor/CustomerUtil.java"));
    }

    @Test
    void customerProxy() {
        CompilationSubject.assertThat(compilation).generatedSourceFile("one.xis.sql.processor.CustomerProxy")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("one/xis/sql/processor/CustomerProxy.java"));
    }

    @Test
    void customerStatements() {
        CompilationSubject.assertThat(compilation).generatedSourceFile("one.xis.sql.processor.CustomerStatements")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("one/xis/sql/processor/CustomerStatements.java"));
    }

    @Test
    void customerResultSets() {
        CompilationSubject.assertThat(compilation).generatedSourceFile("one.xis.sql.processor.CustomerResultSet")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("one/xis/sql/processor/CustomerResultSet.java"));
    }

    @Test
    void customerTableAccessor() {
        CompilationSubject.assertThat(compilation).generatedSourceFile("one.xis.sql.processor.CustomerTableAccessor")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("one/xis/sql/processor/CustomerTableAccessor.java"));
    }

    @Test
    void customerAgentTableAccessor() {
        CompilationSubject.assertThat(compilation).generatedSourceFile("one.xis.sql.processor.CustomerAgentCrossTableAccessor")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("one/xis/sql/processor/CustomerAgentCrossTableAccessor.java"));
    }

    @Test
    void customerCrudHandler() {
        CompilationSubject.assertThat(compilation).generatedSourceFile("one.xis.sql.processor.CustomerCrudHandler")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("one/xis/sql/processor/CustomerCrudHandler.java"));
    }


    @Test
    void customerRepositoryImpl() {
        CompilationSubject.assertThat(compilation).generatedSourceFile("one.xis.sql.processor.CustomerRepositoryImpl")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("one/xis/sql/processor/CustomerRepositoryImpl.java"));
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