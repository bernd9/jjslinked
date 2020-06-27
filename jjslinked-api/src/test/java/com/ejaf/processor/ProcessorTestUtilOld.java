package com.ejaf.processor;

import com.google.testing.compile.Compilation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProcessorTestUtilOld {

    public static void assertSuccess(Compilation compilation) {
        if (compilation.status() != Compilation.Status.SUCCESS) {
            throw new RuntimeException(compilation.errors().stream().map(ProcessorTestUtilOld::asString).collect(Collectors.joining("\n\n")));
        }
    }

    public static String asString(Diagnostic<?> diagnostic) {
        StringBuilder s = new StringBuilder();
        if (diagnostic.getSource() != null)
            s.append(diagnostic.getSource());

        s.append(" in line ").append(diagnostic.getLineNumber())
                .append(": ")
                .append(diagnostic.getMessage(Locale.US)).toString();
        return s.toString();
    }

    public static Collection<String> getSources(Compilation compilation) {
        return compilation.generatedSourceFiles().stream().map(ProcessorTestUtilOld::getSource).collect(Collectors.toList());
    }

    public static String getSource(JavaFileObject fileObject) {
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
