package com.ejc.processor;

import com.ejc.Singleton;
import com.ejc.util.IOUtils;
import com.ejc.util.ProcessorLogger;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.ejc.Singleton"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class CustomSingletonAnnotationProcessor extends AbstractProcessor {

    private static final String PROVIDER_CLASS_PACKAGE = "com.ejc.provider";

    private Set<String> providerClasses = new HashSet<>();


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            roundEnv.getElementsAnnotatedWith(Singleton.class).stream()
                    .filter(e -> e.getKind().equals(ElementKind.ANNOTATION_TYPE))
                    .map(TypeElement.class::cast)
                    .forEach(this::processAnnotation);
        } else {
            writeContextFile();
        }
        return false;
    }

    private void processAnnotation(TypeElement annotation) {
        String simpleName = getProviderClassSimpleName();
        writeProviderClass(annotation, simpleName);
        providerClasses.add(PROVIDER_CLASS_PACKAGE + "." + simpleName);
    }


    private void writeProviderClass(TypeElement annotation, String providerClassSimpleName) {
        CustomAnnotationProviderWriter writer = CustomAnnotationProviderWriter.builder()
                .annotationClass(annotation)
                .processingEnvironment(processingEnv)
                .providerClassPackageName(PROVIDER_CLASS_PACKAGE)
                .providerClassSimpleName(providerClassSimpleName)
                .build();
        writer.write();

    }

    private void writeContextFile() {
        IOUtils.write(providerClasses, processingEnv.getFiler(), "META-INF/services/" + CustomSingletonAnnotationProvider.class.getName());
    }

    private String getProviderClassSimpleName() {
        return "SingletonAnnotationProvider_" + UUID.randomUUID().toString().replace("-", "");
    }


    protected void reportError(Exception e) {
        ProcessorLogger.reportError(this, processingEnv, e);
    }

    protected void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, getClass().getSimpleName() + ": " + String.format(message, args));
    }
}
