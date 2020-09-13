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
import javax.lang.model.type.TypeKind;
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
    private Set<ExecutableElement> beanMethods = new HashSet<>();
    private Set<VariableElement> singleValueDependencies = new HashSet<>();
    private Set<VariableElement> multiValueDependencies = new HashSet<>();
    private Set<VariableElement> configFields = new HashSet<>();
    private Set<TypeElement> singletons = new HashSet<>();
    private Set<TypeElement> configurations = new HashSet<>();
    private Map<TypeElement, TypeElement> implementations = new HashMap<>();
    private String packageName = PACKAGE;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Stream.of(Singleton.class, Inject.class, InjectAll.class, Init.class, Implementation.class,
                Value.class, Application.class, Configuration.class, Bean.class).map(Class::getName).collect(Collectors.toSet());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (!roundEnv.processingOver()) {
                processInitMethods(roundEnv);
                processBeanMethods(roundEnv);
                processSingleValueDependencies(roundEnv);
                processMultiValueDependencies(roundEnv);
                processConfigFields(roundEnv);
                processSingletons(roundEnv);
                processConfigurations(roundEnv);
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
                .peek(e -> validateEnclosedBySingletonOrConfiguration(e, Init.class))
                .collect(Collectors.toSet()));
    }

    private void processBeanMethods(RoundEnvironment roundEnv) {
        beanMethods.addAll(roundEnv.getElementsAnnotatedWith(Bean.class).stream()
                .map(ExecutableElement.class::cast)
                .peek(this::validateNoParameters)
                .peek(e -> validateEnclosedByConfiguration(e, Bean.class))
                .peek(this::validateBeanMethodHasReturnType)
                .collect(Collectors.toSet()));
    }

    private void processSingleValueDependencies(RoundEnvironment roundEnv) {
        singleValueDependencies.addAll(roundEnv.getElementsAnnotatedWith(Inject.class).stream()
                .map(VariableElement.class::cast)
                .peek(e -> validateEnclosedBySingleton(e, Inject.class))
                .collect(Collectors.toSet()));
    }

    private void processMultiValueDependencies(RoundEnvironment roundEnv) {
        multiValueDependencies.addAll(roundEnv.getElementsAnnotatedWith(InjectAll.class).stream()
                .map(VariableElement.class::cast)
                .peek(e -> validateEnclosedBySingleton(e, InjectAll.class))
                .collect(Collectors.toSet()));
    }

    private void processConfigFields(RoundEnvironment roundEnv) {
        configFields.addAll(roundEnv.getElementsAnnotatedWith(Value.class).stream()
                .map(VariableElement.class::cast)
                .collect(Collectors.toSet()));
    }

    private void processSingletons(RoundEnvironment roundEnv) {
        singletons.addAll(roundEnv.getElementsAnnotatedWith(Singleton.class).stream()
                .filter(e -> e.getKind().equals(ElementKind.CLASS))
                .map(TypeElement.class::cast)
                .collect(Collectors.toSet()));
        singletons.addAll(roundEnv.getElementsAnnotatedWith(Singleton.class).stream()
                .filter(e -> e.getKind().equals(ElementKind.ANNOTATION_TYPE))
                .map(TypeElement.class::cast)
                .map(annotation -> roundEnv.getElementsAnnotatedWith(annotation))
                .flatMap(Set::stream)
                .filter(e -> e.getKind().equals(ElementKind.CLASS))
                .map(TypeElement.class::cast)
                .collect(Collectors.toSet()));

    }


    private void processConfigurations(RoundEnvironment roundEnv) {
        log("processing configurations");
        configurations.addAll(roundEnv.getElementsAnnotatedWith(Configuration.class).stream()
                .map(TypeElement.class::cast)
                .collect(Collectors.toSet()));
        log("number of  configurations after processing: " + configurations.size());
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

    private void validateEnclosedBySingleton(Element variableElement, Class<? extends Annotation> reason) {
        if (!variableElement.getEnclosingElement().getKind().equals(ElementKind.CLASS)) {
            throw new IllegalStateException("expected parent element to ba a class: " + variableElement);
        }
        TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
        if (typeElement.getAnnotation(Singleton.class) == null) {
            throw new IllegalStateException(variableElement.getEnclosingElement() + " must be annotated with @Singleton, because it uses " + reason.getSimpleName());
        }
        if (typeElement.getAnnotation(Configuration.class) != null) {
            throw new IllegalStateException(variableElement.getEnclosingElement() + " must be not be annotated with @Configuration, because it uses " + reason.getSimpleName());
        }
    }

    private void validateEnclosedBySingletonOrConfiguration(Element variableElement, Class<? extends Annotation> reason) {
        if (!variableElement.getEnclosingElement().getKind().equals(ElementKind.CLASS)) {
            throw new IllegalStateException("expected parent element to ba a class: " + variableElement);
        }
        TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
        if (typeElement.getAnnotation(Singleton.class) != null) {
            return;
        }
        if (typeElement.getAnnotation(Configuration.class) != null) {
            return;
        }
        throw new IllegalStateException(variableElement.getEnclosingElement() + " must be annotated with @Singleton or Configuration, because it uses " + reason.getSimpleName());
    }

    private void validateEnclosedByConfiguration(Element variableElement, Class<? extends Annotation> reason) {
        if (!variableElement.getEnclosingElement().getKind().equals(ElementKind.CLASS)) {
            throw new IllegalStateException("expected parent element to ba a class: " + variableElement);
        }
        TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
        if (typeElement.getAnnotation(Singleton.class) != null) {
            throw new IllegalStateException(variableElement.getEnclosingElement() + " must not be annotated with @Singleton, because it uses " + reason.getSimpleName());
        }
    }

    private void validateBeanMethodHasReturnType(ExecutableElement element) {
        if (element.getReturnType().getKind() == TypeKind.VOID) {
            throw new IllegalStateException(element + " must not return void. It should return a bean.");
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
                .initMethodsSingleton(getInitMethodsSingleton())
                .initMethodsConfiguration(getInitMethodsConfiguration())
                .multiValueDependencies(multiValueDependencies)
                .singleValueDependencies(singleValueDependencies)
                .configFieldsSingleton(getConfigFieldsSingleton())
                .configFieldsConfiguration(getConfigFieldsConfiguration())
                .singletons(singletons)
                .implementations(implementations)
                .packageName(packageName)
                .processingEnvironment(processingEnv)
                .loadBeanMethods(beanMethods)
                .configurations(configurations)
                .build();
        writer.write();
    }

    private Set<ExecutableElement> getInitMethodsSingleton() {
        return initMethods.stream()
                .filter(e -> e.getEnclosingElement().getAnnotation(Singleton.class) != null)
                .collect(Collectors.toSet());
    }

    private Set<ExecutableElement> getInitMethodsConfiguration() {
        return initMethods.stream()
                .filter(e -> e.getEnclosingElement().getAnnotation(Configuration.class) != null)
                .collect(Collectors.toSet());
    }

    private Set<VariableElement> getConfigFieldsSingleton() {
        return configFields.stream()
                .filter(e -> e.getEnclosingElement().getAnnotation(Singleton.class) != null)
                .collect(Collectors.toSet());
    }


    private Set<VariableElement> getConfigFieldsConfiguration() {
        return configFields.stream()
                .filter(e -> e.getEnclosingElement().getAnnotation(Configuration.class) != null)
                .collect(Collectors.toSet());
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
