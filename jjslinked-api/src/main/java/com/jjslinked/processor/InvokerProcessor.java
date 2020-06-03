package com.jjslinked.processor;

import com.google.auto.service.AutoService;
import com.jjslinked.ast.AstService;
import com.jjslinked.ast.ClassNode;
import com.jjslinked.processor.codegen.HandlebarCodeWriterFactory;
import com.jjslinked.processor.codegen.HandlebarsCodeWriter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.jjslinked.annotations.Client")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class InvokerProcessor extends AbstractProcessor {

    private HandlebarCodeWriterFactory codeWriterFactory;
    private AstService astService;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        codeWriterFactory = new HandlebarCodeWriterFactory("java-templates/Invoker");
        astService = AstService.getInstance();
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.stream().flatMap(annotation -> roundEnv.getElementsAnnotatedWith(annotation).stream())
                .map(TypeElement.class::cast).forEach(this::processClientClass);

        return false;
    }

    private void processClientClass(TypeElement clientElement) {
        ClassNode model = astService.getClassNode(clientElement);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "processing:" + clientElement.getSimpleName());
        try (HandlebarsCodeWriter codeWriter = codeWriterFactory.javaGenerator(model.getQualifiedName() + "Invoker", processingEnv.getFiler())) {
            codeWriter.write(model);
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