package com.ejaf.processor;

import com.google.testing.compile.Compilation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.tools.Diagnostic;
import java.util.Locale;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProcessorTestUtil {

    public static void assertSuccess(Compilation compilation) {
        if (compilation.status() != Compilation.Status.SUCCESS) {
            throw new RuntimeException(compilation.errors().stream().map(ProcessorTestUtil::asString).collect(Collectors.joining("\n\n")));
        }
    }

    public static String asString(Diagnostic<?> diagnostic) {
        return new StringBuilder(diagnostic.getSource().toString())
                .append(" in line ").append(diagnostic.getLineNumber())
                .append(": ")
                .append(diagnostic.getMessage(Locale.US)).toString();

    }

    public static String testResult() {
        /*
        javaFileObject = compilation.generatedSourceFile("com.ejaf.generated.ParameterProvider0").orElseThrow();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(javaFileObject.openInputStream()))) {
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        }

         */
        return null;
    }
}
