package com.jjslinked.processor.receiver;

import com.google.auto.service.AutoService;
import com.jjslinked.ast.ClassNodeBuilder;
import com.jjslinked.ast.MethodNode;
import com.jjslinked.ast.MethodNodeBuilder;
import com.jjslinked.java.ClassElement;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.jjslinked.Receiver")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ReceiverAnnotationProcessor extends AbstractProcessor {

    private final Set<ReceiverInvokerModel> receivers = new HashSet<>();
    private final ReceiverInvokerTemplate invokerTemplate = new ReceiverInvokerTemplate();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        receivers.clear();
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (roundEnv.processingOver()) {
                writeReceiverInvokers();
                writeReceiverRegistry();
            } else {
                receivers.addAll(annotations.stream()
                        .map(roundEnv::getElementsAnnotatedWith)
                        .flatMap(Set::stream)
                        .map(ExecutableElement.class::cast)
                        .map(MethodNodeBuilder::of)
                        .map(this::toReceiverModel)
                        .collect(Collectors.toSet()));
            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }

    private ReceiverInvokerModel toReceiverModel(MethodNode methodNode) {
        return ReceiverInvokerModel.builder()
                .invoker(ClassNodeBuilder.from(methodNode.getDeclaringClass().getQualifiedName() + "Invoker", Collections.emptySet()))
                .methodToInvoke(methodNode).build();
    }

    private ClassElement toInvokerClass(MethodNode methodNode) {
        return ClassElement.builder()
                .packageName(methodNode.getDeclaringClass().getPackageName())
                // TODO.constructorElement(methodNode.getDeclaringClass().)
                //.simpleName(methodNode.getDeclaringClass().getSimpleName())
                .build();
    }

    private void writeReceiverInvokers() {
        receivers.forEach(this::writeReceiverInvoker);
    }

    private void writeReceiverInvoker(ReceiverInvokerModel model) {
        invokerTemplate.write(model, processingEnv.getFiler());
    }

    private void writeReceiverRegistry() {
    }


    private void reportError(Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
    }

    private void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
    }
}