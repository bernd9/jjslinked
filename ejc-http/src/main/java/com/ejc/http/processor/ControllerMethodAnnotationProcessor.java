package com.ejc.http.processor;

import com.ejc.api.context.ClassReference;
import com.ejc.http.BodyContent;
import com.ejc.http.Get;
import com.ejc.http.HttpMethod;
import com.ejc.http.PathVariable;
import com.ejc.http.api.controller.*;
import com.ejc.util.ProcessorUtils;
import com.ejc.util.ReflectionUtils;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ControllerMethodAnnotationProcessor extends AbstractProcessor {


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Stream.of(Get.class).map(Class::getName).collect(Collectors.toSet());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        log("controller-processing ");
        if (!roundEnv.processingOver()) {
            roundEnv.getElementsAnnotatedWith(Get.class).stream()
                    .map(ExecutableElement.class::cast)
                    .forEach(e -> process(e, HttpMethod.GET, () -> e.getAnnotation(Get.class).value()));
            // TODO
        }
        return true;
    }

    private void process(ExecutableElement e, HttpMethod httpMethod, Supplier<String> url) {
        log("processing " + e);
        ControllerMethodWriter writer = ControllerMethodWriter.builder()
                .httpMethod(httpMethod)
                .url(url.get())
                .methodElement(e)
                .packageName(((PackageElement) e.getEnclosingElement().getEnclosingElement()).getQualifiedName().toString()) // TODO Fails for inner classes
                .parameterProviders(getParameterProvider(e))
                .simpleClassName(randomClassName())
                .processingEnvironment(processingEnv)
                .build();
        try {
            writer.write();
        } catch (IOException ex) {
            ProcessorUtils.reportError(this, processingEnv, ex);
        }
    }

    private List<ParameterProvider<?>> getParameterProvider(ExecutableElement e) {
        return e.getParameters().stream()
                .map(this::getParameterProvider)
                .collect(Collectors.toList());
    }

    private ParameterProvider<?> getParameterProvider(VariableElement variableElement) {
        String type = variableElement.asType().toString();
        if (type.equals(ServletRequest.class.getName()) || type.equals(HttpServletRequest.class.getName())) {
            return new ParameterProviderForServletRequest();
        }
        if (type.equals(ServletResponse.class.getName()) || type.equals(HttpServletResponse.class.getName())) {
            return new ParameterProviderForServletResponse();
        }
        if (type.equals(HttpSession.class.getName())) {
            return new ParameterProviderForServletResponse();
        }
        if (variableElement.getAnnotation(BodyContent.class) != null) {
            return new ParameterProviderForRequestBody(ClassReference.getRef(variableElement.asType().toString()));
        }
        if (variableElement.getAnnotation(PathVariable.class) != null) {
            AnnotationMirror pathVariable = ReflectionUtils.getAnnotationMirror(variableElement, PathVariable.class);
            AnnotationValue value = ReflectionUtils.getAnnotationValue(pathVariable, "value");
            return new ParameterProviderForUrlParam(value.getValue().toString(), ClassReference.getRef(variableElement.asType().toString()));
        }
        throw new IllegalStateException("no provider for " + variableElement + " in " + variableElement.getEnclosingElement());
    }

    private String randomClassName() {
        return "Invoker_" + UUID.randomUUID().toString().replace("-", "");
    }

    protected void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, getClass().getSimpleName() + ": " + String.format(message, args));
    }


}