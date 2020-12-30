package com.ejc.sql.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.Compiler.javac;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EntityProcessorTest {

    private Compiler compiler;
    private JavaFileObject address;
    private JavaFileObject order;
    private JavaFileObject customer;
    private JavaFileObject agent;

    @BeforeAll
    void init() {
        compiler = javac().withProcessors(new EntityAnnotationProcessor());
        address = JavaFileObjects.forResource("com/ejc/sql/processor/entity/Address.java");
        order = JavaFileObjects.forResource("com/ejc/sql/processor/entity/Order.java");
        customer = JavaFileObjects.forResource("com/ejc/sql/processor/entity/Customer.java");
        agent = JavaFileObjects.forResource("com/ejc/sql/processor/entity/Agent.java");
    }

    @Test
    void test() {
        Compilation compilation = compiler.compile(address, order, customer, agent);
        assertThat(compilation.status() == Compilation.Status.SUCCESS);
    }


}
