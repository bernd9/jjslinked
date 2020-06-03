package com.jjslinked.processor.codegen;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

class HandlebarCodeWriterFactoryTest {

    @Test
    void iterateHelper() throws IOException {
        StringWriter stringWriter = new StringWriter();
        HandlebarCodeWriterFactory factory = new HandlebarCodeWriterFactory("IterateTest");
        PrintWriter printWriter = new PrintWriter(stringWriter);
        factory.javaGenerator(printWriter).write(new JavaData(List.of(new JavaField("field1"), new JavaField("field2"), new JavaField("field3"))));
    }

    @Test
    void ifEquals() throws IOException {
        StringWriter stringWriter = new StringWriter();
        HandlebarCodeWriterFactory factory = new HandlebarCodeWriterFactory("IfEqualsTest");
        PrintWriter printWriter = new PrintWriter(stringWriter);
        factory.javaGenerator(printWriter).write(new EqualTestData("123", 456));
    }

    @Getter
    @RequiredArgsConstructor
    class EqualTestData {
        private final Object value1;
        private final Object value2;

    }

    @Getter
    @RequiredArgsConstructor
    class JavaData {
        private final List<JavaField> fields;
    }

    @Getter
    @RequiredArgsConstructor
    class JavaField {
        private final String name;
    }

}