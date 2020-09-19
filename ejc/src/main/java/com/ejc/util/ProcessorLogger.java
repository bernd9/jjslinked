package com.ejc.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import java.io.PrintWriter;
import java.io.StringWriter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProcessorLogger {
    
    public static void reportError(AbstractProcessor processor, ProcessingEnvironment environment, Exception e) {
        environment.getMessager().printMessage(Diagnostic.Kind.ERROR, processor.getClass().getSimpleName() + ": " + e.toString() + "\n" + stacktrace(e));
    }

    private static String stacktrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
