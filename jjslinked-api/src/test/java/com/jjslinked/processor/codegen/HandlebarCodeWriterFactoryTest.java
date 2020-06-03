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