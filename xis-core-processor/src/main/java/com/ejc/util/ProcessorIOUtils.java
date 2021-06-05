package com.ejc.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProcessorIOUtils {


    public static void write(Collection<String> lines, Filer filer, String resource) {
        write(lines, registryTextFileForWriting(resource, filer));
    }

    // TODO move this method to processor utils
    public static void write(Collection<String> lines, FileObject fileObject) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(fileObject.openOutputStream()))) {
            lines.forEach(writer::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static FileObject registryTextFileForWriting(String name, Filer filer) {
        try {
            return filer.createResource(StandardLocation.CLASS_OUTPUT, "", name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
