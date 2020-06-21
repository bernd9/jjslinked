package com.jjslinked.processor.receiver;

import com.google.auto.service.AutoService;
import com.jjslinked.ast.ClassNodeBuilder;
import com.jjslinked.ast.MethodNode;
import com.jjslinked.ast.MethodNodeBuilder;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.jjslinked.Receiver")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ReceiverAnnotationProcessor extends AbstractProcessor {

    private final ReceiverInvokerTemplate invokerTemplate = new ReceiverInvokerTemplate();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (!roundEnv.processingOver()) {
                annotations.stream()
                        .map(roundEnv::getElementsAnnotatedWith)
                        .flatMap(Set::stream)
                        .map(ExecutableElement.class::cast)
                        .map(MethodNodeBuilder::of)
                        .map(this::toReceiverModel)
                        .forEach(this::writeReceiverInvoker);

            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }

    private ReceiverInvokerModel toReceiverModel(MethodNode methodNode) {
        return ReceiverInvokerModel.builder()
                .invoker(ClassNodeBuilder.from(methodNode.getDeclaringClass().getQualifiedName() + "Invoker" + randomString(), Collections.emptySet()))
                .methodToInvoke(methodNode).build();
    }

    private void writeReceiverInvoker(ReceiverInvokerModel model) {
        invokerTemplate.write(model, processingEnv.getFiler());
    }

    private String randomString() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    private void reportError(Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
    }

    private void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
    }
}