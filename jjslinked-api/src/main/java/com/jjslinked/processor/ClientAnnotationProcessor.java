package com.jjslinked.processor;

import com.google.auto.service.AutoService;
import com.jjslinked.annotations.Client;
import com.jjslinked.processor.codegen.java.ClientImplCodeGenerator;
import com.jjslinked.processor.codegen.java.ClientImplModel;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.jjslinked.annotations.Client")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ClientAnnotationProcessor extends AbstractProcessor {

    private ClientImplCodeGenerator clientImplCodeGenerator;
    private Set<TypeElement> clientClasses;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        clientImplCodeGenerator = new ClientImplCodeGenerator();
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (clientClasses == null) {
            clientClasses = roundEnv.getElementsAnnotatedWith(Client.class).stream().map(TypeElement.class::cast).collect(Collectors.toSet());
        }
        if (roundEnv.processingOver()) {
            createImplementations();
        }
        return false;
    }


    private void createImplementations() {
        try {
            clientClasses.forEach(this::createImplementation);
        } catch (Exception e) {
            reportError(e);
        }
    }

    private void createImplementation(TypeElement e) {
        log("creating implementation for", e);
        try {
            createImplementation(new ClientImplModel(e));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void createImplementation(ClientImplModel model) throws IOException {
        createImplementation(model, processingEnv.getFiler().createSourceFile(model.getImplementationClassName()));
    }

    private void createImplementation(ClientImplModel model, JavaFileObject fileObject) throws IOException {
        try (PrintWriter out = new PrintWriter(fileObject.openOutputStream())) {
            clientImplCodeGenerator.write(model, out);
        }
    }

    private void reportError(Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
    }

    private void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, getClass().getSimpleName() + ":" + String.format(message, args));
    }


}