package com.jjslinked.processor.receiver;

import com.google.auto.service.AutoService;
import com.jjslinked.model.ClassModel;
import com.jjslinked.model.MethodModel;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
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
                        .map(MethodModel::fromElement)
                        .map(this::toReceiverModel)
                        .collect(Collectors.toSet()));
            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }

    private ReceiverInvokerModel toReceiverModel(MethodModel methodModel) {
        return ReceiverInvokerModel.builder()
                .invoker(ClassModel.fromName(methodModel.getDeclaringClass().getQualifiedName() + "Invoker"))
                .methodToInvoke(methodModel).build();
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