package com.jjslinked.processor.codegen.java;

import com.jjslinked.processor.codegen.MustacheCodeWriterFactory;

import javax.annotation.processing.Filer;

public class ClientServiceImplCodeWriterFactory extends MustacheCodeWriterFactory {

    public ClientServiceImplCodeWriterFactory(Filer filer) {
        super("java/ClientService.mustache", filer);
    }
}
