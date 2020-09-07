package com.ejc.processor;

import com.ejc.*;
import com.ejc.api.context.Undefined;
import com.ejc.util.IOUtils;
import com.ejc.util.ProcessorUtils;
import com.ejc.util.ReflectionUtils;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ejc.util.ReflectionUtils.getAnnotationMirrorOptional;
import static com.ejc.util.ReflectionUtils.getAnnotationValue;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ApplicationContextFactoryProcessor extends AbstractProcessor {

    static final String PACKAGE = "com.ejc.generated";

    private Set<ExecutableElement> initMethods = new HashSet<>();
    private Set<VariableElement> singleValueDependencies = new HashSet<>();
    private Set<VariableElement> multiValueDependencies = new HashSet<>();
    private Set<VariableElement> configValues = new HashSet<>();
    private Set<TypeElement> singletons = new HashSet<>();
    private Map<TypeElement, TypeElement> implementations = new HashMap<>();
    private String packageName = PACKAGE;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Stream.of(Singleton.class, Inject.class, InjectAll.class, Init.class, Implementation.class, Value.class, Application.class).map(Class::getName).collect(Collectors.toSet());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        initMethods.clear();
        singleValueDependencies.clear();
        multiValueDependencies.clear();
        singletons.clear();
        implementations.clear();
        super.init(processingEnv);
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (!roundEnv.processingOver()) {
                processInitMethods(roundEnv);
                processSingleValueDependencies(roundEnv);
                processMultiValueDependencies(roundEnv);
                processConfigValues(roundEnv);
                processSingletons(roundEnv);
                processImplementations(roundEnv);
                processApplication(roundEnv);
            } else {
                writeApplicationContextFactory();
                writeContextFile();
            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }

    private void processInitMethods(RoundEnvironment roundEnv) {
        initMethods.addAll(roundEnv.getElementsAnnotatedWith(Init.class).stream()
                .map(ExecutableElement.class::cast)
                .peek(this::validateNoParameters)
                .collect(Collectors.toSet()));
    }

    private void processSingleValueDependencies(RoundEnvironment roundEnv) {
        singleValueDependencies.addAll(roundEnv.getElementsAnnotatedWith(Inject.class).stream()
                .map(VariableElement.class::cast)
                .collect(Collectors.toSet()));
    }

    private void processMultiValueDependencies(RoundEnvironment roundEnv) {
        multiValueDependencies.addAll(roundEnv.getElementsAnnotatedWith(InjectAll.class).stream()
                .map(VariableElement.class::cast)
                .collect(Collectors.toSet()));
    }

    private void processConfigValues(RoundEnvironment roundEnv) {
        configValues.addAll(roundEnv.getElementsAnnotatedWith(Value.class).stream()
                .map(VariableElement.class::cast)
                .collect(Collectors.toSet()));
    }

    private void processSingletons(RoundEnvironment roundEnv) {
        singletons.addAll(roundEnv.getElementsAnnotatedWith(Singleton.class).stream()
                .map(TypeElement.class::cast)
                .collect(Collectors.toSet()));
    }


    private void processImplementations(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(Implementation.class).stream()
                .map(TypeElement.class::cast)
                .forEach(impl -> implementations.put(getSuperClassToOverride(impl), impl));
    }

    private TypeElement getSuperClassToOverride(TypeElement e) {
        AnnotationMirror annotationMirror = getAnnotationMirrorOptional(e, Implementation.class).orElseThrow();
        return forClassByName(annotationMirror).orElseGet(() -> forClassByClass(annotationMirror).orElseThrow(() -> new IllegalStateException("Implementation requires \"forClass\" or \"forClassName\"")));
    }

    private Optional<TypeElement> forClassByClass(AnnotationMirror annotationMirror) {
        return Optional.ofNullable(getAnnotationValue(annotationMirror, "forClass"))
                .filter(Objects::nonNull)
                .map(AnnotationValue::getValue)
                .map(Class.class::cast) // TODO Test
                .filter(c -> !c.equals(Undefined.class))
                .map(Class::getName)
                .map(processingEnv.getElementUtils()::getTypeElement);
    }

    private Optional<TypeElement> forClassByName(AnnotationMirror annotationMirror) {
        return Optional.ofNullable(getAnnotationValue(annotationMirror, "forClassName"))
                .filter(Objects::nonNull)
                .map(AnnotationValue::getValue)
                .map(Object::toString)
                .filter(s -> !s.isBlank())
                .map(processingEnv.getElementUtils()::getTypeElement);
    }


    private void validateNoParameters(ExecutableElement element) {
        if (!element.getParameters().isEmpty()) {
            throw new IllegalStateException(element + " must have no parameters");
        }
    }

    private Optional<PackageElement> getGeneratedClassPackage(Class<? extends Annotation> a, RoundEnvironment roundEnvironment) {
        Set<? extends Element> e = roundEnvironment.getElementsAnnotatedWith(a);
        if (e.size() > 0) {
            return Optional.of(e.iterator().next()).map(Element::getEnclosingElement).map(PackageElement.class::cast);
        }
        return Optional.empty();
    }

    private void processApplication(RoundEnvironment roundEnv) {
        List<TypeElement> classes = roundEnv.getElementsAnnotatedWith(Application.class).stream()
                .map(TypeElement.class::cast)
                .collect(Collectors.toList());
        TypeElement appClass;
        switch (classes.size()) {
            case 0:
                return;
            case 1:
                appClass = classes.get(0);
                break;
            default:
                throw new IllegalStateException("Multiple Application-annotations");
        }

        packageName = ReflectionUtils.getPackageName(appClass.getQualifiedName());
    }

    private String factoryQualifiedName() {
        return packageName + "." + ApplicationContextFactory.IMPLEMENTATION_SIMPLE_NAME;
    }

    private void writeApplicationContextFactory() throws IOException {
        ApplicationContextFactoryWriter writer = ApplicationContextFactoryWriter.builder()
                .initMethods(initMethods)
                .multiValueDependencies(multiValueDependencies)
                .singleValueDependencies(singleValueDependencies)
                .configValues(configValues)
                .singletons(singletons)
                .implementations(implementations)
                .packageName(packageName)
                .processingEnvironment(processingEnv)
                .build();
        writer.write();
    }

    private void writeContextFile() {
        IOUtils.write(Collections.singletonList(factoryQualifiedName()), processingEnv.getFiler(), "META-INF/services/" + ApplicationContextFactory.class.getName());
    }


    protected void reportError(Exception e) {
        ProcessorUtils.reportError(this, processingEnv, e);
    }

    protected void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, getClass().getSimpleName() + ": " + String.format(message, args));
    }

}
