package com.jjslinked.processor.receiver;

import com.google.auto.service.AutoService;
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

    private final Set<MethodModel> receivers = new HashSet<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {

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
                        .collect(Collectors.toSet()));
            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }

    private void writeReceiverInvokers() {
        receivers.forEach(this::writeReceiverInvoker);
    }

    private void writeReceiverInvoker(MethodModel methodModel) {
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