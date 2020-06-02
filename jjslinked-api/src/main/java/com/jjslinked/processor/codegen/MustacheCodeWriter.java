package com.jjslinked.processor.codegen;

import com.github.mustachejava.Mustache;

import javax.tools.FileObject;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class MustacheCodeWriter implements Closeable {

    private final FileObject fileObject;
    private final Mustache mustache;
    private Writer writer;


    public MustacheCodeWriter(FileObject fileObject, Mustache mustache) {
        this.fileObject = fileObject;
        this.mustache = mustache;
        try {
            this.writer = new PrintWriter(fileObject.openOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(Object context) {
        mustache.execute(writer, context);
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
