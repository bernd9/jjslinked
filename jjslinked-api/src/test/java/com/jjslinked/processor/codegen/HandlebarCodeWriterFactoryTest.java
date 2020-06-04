package com.jjslinked.processor.codegen;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

class HandlebarCodeWriterFactoryTest {

    @Test
    void iterateHelper() throws IOException {
        StringWriter stringWriter = new StringWriter();
        HandlebarCodeWriterFactory factory = new HandlebarCodeWriterFactory("handlebar/IterateTest");
        PrintWriter printWriter = new PrintWriter(stringWriter);

        factory.javaGenerator(printWriter).write(new TestData(List.of(new TestField("field1"), new TestField("field2"), new TestField("field3"))));

        String result = stringWriter.toString();
        assertTrue(result.contains("field1"));
        assertTrue(result.contains("field2"));
        assertTrue(result.contains("field3"));
    }

    @Test
    void ifEquals() throws IOException {
        StringWriter stringWriter = new StringWriter();
        HandlebarCodeWriterFactory factory = new HandlebarCodeWriterFactory("handlebar/IfEqualsTest");
        PrintWriter printWriter = new PrintWriter(stringWriter);
        factory.javaGenerator(printWriter).write(new EqualTestData("123", 123));

        String result = stringWriter.toString();
        assertTrue(result.contains("yes"));
        assertTrue(result.contains("super"));
        assertFalse(result.contains("wrong"));
    }

    @Test
    void setVarGetVar() throws IOException {
        StringWriter stringWriter = new StringWriter();
        HandlebarCodeWriterFactory factory = new HandlebarCodeWriterFactory("handlebar/SetVarGetVarTest");
        PrintWriter printWriter = new PrintWriter(stringWriter);
        factory.javaGenerator(printWriter).write(new Object());

        String result = stringWriter.toString();
        assertTrue(result.contains("123"));
    }

    @Getter
    @RequiredArgsConstructor
    class EqualTestData {
        private final Object value1;
        private final Object value2;

    }

    @Getter
    @RequiredArgsConstructor
    class TestData {
        private final List<TestField> fields;
    }

    @Getter
    @RequiredArgsConstructor
    class TestField {
        private final String name;
    }

}