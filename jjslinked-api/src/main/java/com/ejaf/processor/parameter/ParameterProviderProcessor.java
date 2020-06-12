package com.ejaf.processor.parameter;

import com.ejaf.Provider;
import com.ejaf.processor.InternalEvents;
import com.google.auto.service.AutoService;
import com.jjslinked.processor.util.AnnotationUtil;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.ejaf.Provider")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ParameterProviderProcessor extends AbstractProcessor {

    private ParameterProviderRegistryTemplate parameterProviderTemplate = new ParameterProviderRegistryTemplate();
    private Map<String, String> providerMapping = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        providerMapping.clear();
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (roundEnv.processingOver()) {
                parameterProviderTemplate.write(new ParameterProviderRegistryModel(providerMapping), processingEnv.getFiler());
                InternalEvents.fireEvent(new ParameterProvidersProcessedEvent());
            } else {
                getCustomProviderAnnotation(roundEnv)
                        .peek(this::validateCustomAnnotation)
                        .forEach(this::mapParameterProvider);
            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }

    private void validateCustomAnnotation(TypeElement element) {
    }


    private void mapParameterProvider(TypeElement annotation) {
        providerMapping.put(annotation.asType().toString(), getParameterProvider(annotation));
    }

    private String getParameterProvider(TypeElement customProverAnnotation) {
        return AnnotationUtil.getAnnotationAttribute(customProverAnnotation, Provider.class, "value").orElseThrow();

    }

    private Stream<TypeElement> getCustomProviderAnnotation(RoundEnvironment roundEnvironment) {
        return roundEnvironment.getElementsAnnotatedWith(Provider.class).stream()
                .map(TypeElement.class::cast);
    }

    private void reportError(Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
    }

    private void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
    }
}