package com.ejaf.processor.parameter;

import com.ejaf.ParameterProviderAnnotation;
import com.ejaf.processor.ClassCreatedEvents;
import com.ejaf.processor.annotations.AnnotationUtil;
import com.ejaf.util.CodeGeneratorUtils;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.util.*;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.ejaf.ParameterProviderAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ParameterProviderProcessor extends AbstractProcessor {

    private Map<TypeElement, Collection<VariableElement>> annotatedParameters = new HashMap<>();
    private ParameterProviderTemplate parameterProviderTemplate = new ParameterProviderTemplate();
    private int instanceId;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        instanceId = 0;
        annotatedParameters.clear();
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (roundEnv.processingOver()) {
                annotatedParameters.entrySet().stream().forEach(e -> processParameters(e.getKey(), e.getValue()));
            } else {
                Set<TypeElement> customAnnotations = getCustomAnnotation(roundEnv);
                validateCustomAnnotations(customAnnotations);
                customAnnotations.forEach(annotation -> mapAnnotatedParameters(annotation, roundEnv));
            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }


    private void mapAnnotatedParameters(TypeElement annotation, RoundEnvironment roundEnv) {
        annotatedParameters.computeIfAbsent(annotation, a -> new ArrayList<>()).addAll(findAnnotatedParameters(annotation, roundEnv));
    }

    private Collection<VariableElement> findAnnotatedParameters(TypeElement annotation, RoundEnvironment roundEnv) {
        return roundEnv.getElementsAnnotatedWith(annotation).stream().map(VariableElement.class::cast).collect(Collectors.toSet());
    }

    private void processParameters(TypeElement annotation, Collection<VariableElement> parameters) {
        parameters.forEach(parameter -> processParameter(annotation, parameter));
    }

    private void processParameter(TypeElement annotation, VariableElement parameter) {
        log("processing parameter %s", parameter);
        ParameterProviderModel model = createParameterProviderModel(annotation, parameter);
        parameterProviderTemplate.write(model, processingEnv.getFiler());
        ClassCreatedEvents.fireClassCreatedEvent(model);
    }

    private ParameterProviderModel createParameterProviderModel(TypeElement annotation, VariableElement parameter) {
        ExecutableElement enclosingMethod = (ExecutableElement) parameter.getEnclosingElement();
        TypeElement enclosingType = (TypeElement) enclosingMethod.getEnclosingElement();
        String parameterProvider = getParameterProvider(annotation);
        return ParameterProviderModel.builder()
                .methodRef(enclosingMethod.getSimpleName().toString())
                .typeRef(enclosingType.getQualifiedName().toString())
                .paramRef(parameter.getSimpleName().toString())
                .providerSuperClassPackageName(CodeGeneratorUtils.getPackageName(parameterProvider))
                .providerSuperClassSimpleName(CodeGeneratorUtils.getSimpleName(parameterProvider))
                .providerClassPackageName("com.ejaf.generated")
                .providerClassSimpleName(providerClassSimpleName())
                .build();
    }

    private String getParameterProvider(TypeElement customProverAnnotation) {
        return AnnotationUtil.getAnnotationAttribute(customProverAnnotation, ParameterProviderAnnotation.class, "value").orElseThrow();

    }

    private String providerClassSimpleName() {
        return String.format("ParameterProvider%d", instanceId++);
    }

    private Set<TypeElement> getCustomAnnotation(RoundEnvironment roundEnvironment) {
        return roundEnvironment.getElementsAnnotatedWith(ParameterProviderAnnotation.class).stream()
                .map(TypeElement.class::cast)
                .collect(Collectors.toSet());
    }

    private void validateCustomAnnotations(Set<TypeElement> annotations) {
        // TODO
    }


    private void reportError(Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
    }

    private void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
    }


}