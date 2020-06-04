package com.jjslinked.processor2;

import com.google.auto.service.AutoService;
import com.jjslinked.annotations.Client;
import com.jjslinked.ast.AstService;
import com.jjslinked.processor.codegen.HandlebarCodeWriterFactory;
import com.jjslinked.processor.codegen.HandlebarsCodeWriter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.jjslinked.annotations.Client")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ClientAnnotationProcessor extends AbstractProcessor {

    private HandlebarCodeWriterFactory codeWriterFactory;
    private Collection<TypeElement> clientClasses;
    private AstService astService;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        codeWriterFactory = new HandlebarCodeWriterFactory("java-templates/InvokerDispatcherImpl");
        astService = AstService.getInstance();
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (clientClasses == null) {
            clientClasses = roundEnv.getElementsAnnotatedWith(Client.class).stream().map(TypeElement.class::cast).collect(Collectors.toSet());
        }
        if (roundEnv.processingOver()) {
            createDispatcher();
        }
        return false;
    }


    private void createDispatcher() {
        try (HandlebarsCodeWriter codeWriter = codeWriterFactory.javaGenerator("jjslinked.generated.InvokerDispatcherImpl", processingEnv.getFiler())) {
            codeWriter.write(Collections.singletonMap("clientClasses", clientClasses.stream().map(astService::getClassNode).collect(Collectors.toList())));
        } catch (IOException e) {
            reportError(e);
        }
    }


    private void reportError(Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
    }

    private void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
    }


}