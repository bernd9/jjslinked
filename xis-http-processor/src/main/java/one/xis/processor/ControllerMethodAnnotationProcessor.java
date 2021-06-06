package one.xis.processor;

import one.xis.context.ClassReference;
import com.ejc.http.*;
import com.ejc.http.api.controller.*;
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
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ejc.util.JavaModelUtils.getAnnotationMirror;
import static com.ejc.util.JavaModelUtils.getAnnotationValue;
import static one.xis.processor.ProcessorLogger.reportError;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ControllerMethodAnnotationProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Stream.of(Get.class, Delete.class, Options.class, Post.class, Put.class, Trace.class, Http.class).map(Class::getName).collect(Collectors.toSet());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        log("controller-processing ");
        if (!roundEnv.processingOver()) {
            process(Get.class, HttpMethod.GET, e -> e.getAnnotation(Get.class).value(), roundEnv);
            process(Put.class, HttpMethod.PUT, e -> e.getAnnotation(Put.class).value(), roundEnv);
            process(Post.class, HttpMethod.POST, e -> e.getAnnotation(Post.class).value(), roundEnv);
            process(Delete.class, HttpMethod.DELETE, e -> e.getAnnotation(Delete.class).value(), roundEnv);
            process(Trace.class, HttpMethod.TRACE, e -> e.getAnnotation(Trace.class).value(), roundEnv);
            process(Options.class, HttpMethod.OPTIONS, e -> e.getAnnotation(Options.class).value(), roundEnv);
            process(Http.class, null, e -> e.getAnnotation(Http.class).value(), roundEnv);
        }
        return true;
    }

    private void process(Class<? extends Annotation> annotationClass, HttpMethod httpMethod, Function<ExecutableElement, String> urlFunction, RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(annotationClass).stream()
                .map(ExecutableElement.class::cast)
                .forEach(e -> process(e, httpMethod, urlFunction.apply(e)));
    }


    private void process(ExecutableElement e, HttpMethod httpMethod, String url) {
        log("processing " + e);
        var writer = ControllerMethodWriter.builder()
                .httpMethod(httpMethod)
                .methodUrl(url)
                .classUrl(getClassUrl((TypeElement) e.getEnclosingElement()))
                .methodElement(e)
                .packageName(((PackageElement) e.getEnclosingElement().getEnclosingElement()).getQualifiedName().toString()) // TODO Fails for inner classes
                .parameterProviders(getParameterProvider(e))
                .simpleClassName(randomClassName())
                .processingEnvironment(processingEnv)
                .build();
        try {
            writer.write();
        } catch (IOException ex) {
            reportError(this, processingEnv, ex);
        }
    }

    private String getClassUrl(TypeElement controllerClass) {
        return controllerClass.getAnnotation(RestController.class).value();
    }

    private List<ParameterProvider<?>> getParameterProvider(ExecutableElement e) {
        return e.getParameters().stream()
                .map(this::getParameterProvider)
                .collect(Collectors.toList());
    }

    private ParameterProvider<?> getParameterProvider(VariableElement variableElement) {
        var type = variableElement.asType().toString();
        if (type.equals(ServletRequest.class.getName()) || type.equals(HttpServletRequest.class.getName())) {
            return new ParameterProviderForServletRequest();
        }
        if (type.equals(ServletResponse.class.getName()) || type.equals(HttpServletResponse.class.getName())) {
            return new ParameterProviderForServletResponse();
        }
        if (type.equals(HttpSession.class.getName())) {
            return new ParameterProviderForSession();
        }
        if (variableElement.getAnnotation(BodyContent.class) != null) {
            return new ParameterProviderForRequestBody(ClassReference.getRef(variableElement.asType().toString()));
        }
        if (variableElement.getAnnotation(PathVariable.class) != null) {
            AnnotationMirror pathVariable = getAnnotationMirror(variableElement, PathVariable.class);
            AnnotationValue value = getAnnotationValue(pathVariable, "value");
            return new ParameterProviderForUrlParam(value.getValue().toString(), ClassReference.getRef(variableElement.asType().toString()));
        }
        if (variableElement.getAnnotation(NamedPart.class) != null) {
            AnnotationMirror namedPart = getAnnotationMirror(variableElement, NamedPart.class);
            AnnotationValue value = getAnnotationValue(namedPart, "value");
            return new ParameterProviderForQueryParameter(value.getValue().toString(), ClassReference.getRef(variableElement.asType().toString()));
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