package com.ejc.processor;

import com.ejc.*;
import com.ejc.api.context.UndefinedClass;
import com.ejc.processor.model.Singletons;
import com.ejc.util.CollectorUtils;
import com.ejc.util.IOUtils;
import com.ejc.util.JavaModelUtils;
import com.google.auto.service.AutoService;
import com.google.common.base.Functions;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ejc.util.JavaModelUtils.getAnnotationMirrorOptional;
import static com.ejc.util.JavaModelUtils.getAnnotationValue;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class SingletonProcessor extends ProcessorBase {
    static final String PACKAGE = "com.ejc.generated";

    private Map<Element, ExecutableElement> initMethods = new HashMap<>();
    private Map<Element, ExecutableElement> beanMethods = new HashMap<>();
    private Map<Element, VariableElement> dependencyFields = new HashMap<>();
    private Map<Element, VariableElement> configFields = new HashMap<>();
    private Set<TypeElement> singletons = new HashSet<>();
    private Map<TypeElement, TypeElement> implementations = new HashMap<>();

    private String packageName = PACKAGE;

    private static final Set<String> NON_SINGLETON_ANNOTATIONS = Stream.of(Inject.class, InjectAll.class, Init.class, Implementation.class,
            Value.class, Application.class, Configuration.class, Bean.class).map(Class::getName).collect(Collectors.toSet());
    private Set<String> singletonAnnotations;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        singletonAnnotations = new HashSet<>();
        singletonAnnotations.add(Singleton.class.getName());
        singletonAnnotations.add(Configuration.class.getName());
        singletonAnnotations.addAll(CustomSingletonAnnotationLoader.load());
        Set<String> types = new HashSet<>();
        types.addAll(singletonAnnotations);
        types.addAll(NON_SINGLETON_ANNOTATIONS);
        return types;
    }

    @Override
    protected void process(ProcessingResult result) {
        singletons.addAll(singletonAnnotations.stream()
                .map(name -> result.getElements(name, TypeElement.class))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()));
        initMethods.putAll(result.getElements(Init.class, ExecutableElement.class).stream()
                .collect(Collectors.toMap(Element::getEnclosingElement, Functions.identity())));
        beanMethods.putAll(result.getElements(Bean.class, ExecutableElement.class).stream()
                .collect(Collectors.toMap(Element::getEnclosingElement, Functions.identity())));
        dependencyFields.putAll(result.getElements(Inject.class, VariableElement.class).stream()
                .collect(Collectors.toMap(Element::getEnclosingElement, Functions.identity())));
        configFields.putAll(result.getElements(Value.class, VariableElement.class).stream()
                .collect(Collectors.toMap(Element::getEnclosingElement, Functions.identity())));
        implementations.putAll(result.getElements(Implementation.class, TypeElement.class).stream()
                .collect(Collectors.toMap(Functions.identity(), this::getSuperClassToOverride)));
    }

    private SingletonWriterModels createWriterModel() {
        Map<TypeElement, List<TypeElement>> hierarchy = singletons.stream()
                .collect(Collectors.toMap(Functions.identity(), this::getClassHierarchy));
        SingletonWriterModels model = new SingletonWriterModels(hierarchy);
        initMethods.forEach(model::putInitMethod);
        beanMethods.forEach(model::putBeanMethod);
        dependencyFields.forEach(model::putDependencyField);
        configFields.forEach(model::putConfigField);
        implementations.forEach(model::putImplementation);
        singletons.stream().forEach(singleton -> model.putConstructor(singleton, getConstructor(singleton)));
        return model;
    }

    @Override
    protected void processingOver() {
        writeSingletons(createWriterModel());
        writeContextFile();
    }

    private void writeSingletons(SingletonWriterModels singletonWriterModels) {
        SingletonsWriter writer = SingletonsWriter.builder()
                .model(singletonWriterModels)
                .packageName(packageName)
                .processingEnvironment(processingEnv)
                .build();
        try {
            writer.write();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<TypeElement> getClassHierarchy(TypeElement typeElement) {
        List<TypeElement> hierarchy = new LinkedList<>();
        TypeElement t = typeElement;
        while (t != null && !t.getQualifiedName().toString().equals(Object.class.getName())) {
            hierarchy.add(t);
            t = (TypeElement) processingEnv.getTypeUtils().asElement(t.getSuperclass());
        }
        return hierarchy;
    }

    private static ExecutableElement getConstructor(TypeElement typeElement) {
        return typeElement.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.CONSTRUCTOR)
                .map(ExecutableElement.class::cast)
                .collect(CollectorUtils.toSingleton(() -> new IllegalStateException(typeElement + " has multiple constructors")));
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
                .filter(c -> !c.equals(UndefinedClass.class))
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

        packageName = JavaModelUtils.getPackageName(appClass.getQualifiedName());
    }

    private String factoryQualifiedName() {
        return packageName + "." + ApplicationContextFactory.IMPLEMENTATION_SIMPLE_NAME;
    }

    private void writeApplicationContextFactory() throws IOException {

    }


    private void writeContextFile() {
        IOUtils.write(Collections.singletonList(factoryQualifiedName()), processingEnv.getFiler(), "META-INF/services/" + Singletons.class.getName());
    }


}
