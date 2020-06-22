package com.injectlight.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.*;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IOUtil {

    public static void addLines(Collection<String> lines, Filer filer, String resource) {
        Set<String> set = new HashSet<>(read(filer, resource));
        set.addAll(lines);
        write(lines, filer, resource);
    }

    public static Collection<String> read(Filer filer, String resource) {
        return read(fileObjectForReading(resource, filer));
    }

    public static void write(Collection<String> lines, Filer filer, String resource) {
        write(lines, registryTextFileForWriting(resource, filer));
    }


    public static Collection<String> read(FileObject fileObject) {
        List<String> lines = new LinkedList<>();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(fileObject.openInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (IOException e) {

        }
        return lines;
    }

    public static void write(Collection<String> lines, FileObject fileObject) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(fileObject.openOutputStream()))) {
            lines.forEach(writer::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static FileObject fileObjectForReading(String name, Filer filer) {
        try {
            return filer.getResource(StandardLocation.CLASS_OUTPUT, "", name);
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
