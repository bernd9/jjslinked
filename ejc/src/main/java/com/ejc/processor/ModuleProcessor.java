package com.ejc.processor;

import com.ejc.*;
import com.ejc.api.context.ModuleFactory;
import com.ejc.api.context.UndefinedClass;
import com.ejc.util.CollectionUtils;
import com.ejc.util.CollectorUtils;
import com.ejc.util.IOUtils;
import com.google.auto.service.AutoService;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ejc.util.JavaModelUtils.getAnnotationMirrorOptional;
import static com.ejc.util.JavaModelUtils.getAnnotationValue;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ModuleProcessor extends ProcessorBase {

    private Map<Name, Collection<ExecutableElement>> initMethods = new HashMap<>();
    private Map<Name, Collection<ExecutableElement>> beanMethods = new HashMap<>();
    private Map<Name, Collection<VariableElement>> dependencyFields = new HashMap<>();
    private Map<Name, Collection<VariableElement>> collectionDependencyFields = new HashMap<>();
    private Map<Name, Collection<VariableElement>> configFields = new HashMap<>();
    private Set<TypeElement> singletons = new HashSet<>();
    private Map<TypeElement, TypeElement> implementations = new HashMap<>();

    private Set<String> appClassQualifiedNames = new HashSet<>();

    private static final Set<String> NON_SINGLETON_ANNOTATIONS = Stream.of(Inject.class, Init.class, Implementation.class,
            Value.class, Configuration.class, Bean.class).map(Class::getName).collect(Collectors.toSet());
    private Set<String> singletonAnnotations;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        singletonAnnotations = new HashSet<>();
        singletonAnnotations.add(Singleton.class.getName());
        singletonAnnotations.add(Configuration.class.getName());
        singletonAnnotations.add(Implementation.class.getName());
        singletonAnnotations.add(Application.class.getName());
        singletonAnnotations.addAll(CustomSingletonAnnotationLoader.load());
        Set<String> types = new HashSet<>();
        types.addAll(singletonAnnotations);
        types.addAll(NON_SINGLETON_ANNOTATIONS);
        return types;
    }

    @Override
    protected void process(QueryResult result) {
        processSingletons(result);
        processInitMethods(result);
        processBeanMethods(result);
        processInjectFields(result);
        processValueFields(result);
        processImplementations(result);
        processApplication(result);
    }

    private void processSingletons(QueryResult result) {
        singletons.addAll(singletonAnnotations.stream()
                .map(name -> result.getElements(name, TypeElement.class))
                .flatMap(Collection::stream)
                .filter(e -> e.getKind() != ElementKind.ANNOTATION_TYPE)
                .collect(Collectors.toSet()));
    }


    private void processImplementations(QueryResult result) {
        implementations.putAll(result.getElements(Implementation.class, TypeElement.class).stream()
                .collect(Collectors.toMap(Functions.identity(), this::getSuperClassToOverride)));
    }

    private void processValueFields(QueryResult result) {
        result.getElements(Value.class, VariableElement.class).forEach(this::processValueField);
    }

    private void processValueField(VariableElement field) {
        TypeElement owner = (TypeElement) field.getEnclosingElement();
        configFields.computeIfAbsent(owner.getQualifiedName(), t -> new HashSet<>()).add(field);
    }

    private void processInjectFields(QueryResult result) {
        var fields = result.getElements(Inject.class, VariableElement.class);
        fields.stream().filter(isCollection()).forEach(this::processInjectCollectionField);
        fields.stream().filter(isCollection().negate()).forEach(this::processInjectField);
    }

    private void processInjectField(VariableElement field) {
        TypeElement owner = (TypeElement) field.getEnclosingElement();
        dependencyFields.computeIfAbsent(owner.getQualifiedName(), t -> new HashSet<>()).add(field);
    }

    private void processInjectCollectionField(VariableElement field) {
        TypeElement owner = (TypeElement) field.getEnclosingElement();
        collectionDependencyFields.computeIfAbsent(owner.getQualifiedName(), t -> new HashSet<>()).add(field);
    }

    private void processBeanMethods(QueryResult result) {
        result.getElements(Bean.class, ExecutableElement.class).stream()
                .forEach(this::processBeanMethod);
    }

    private void processBeanMethod(ExecutableElement method) {
        TypeElement owner = (TypeElement) method.getEnclosingElement();
        beanMethods.computeIfAbsent(owner.getQualifiedName(), name -> new HashSet()).add(method);
    }

    private void processInitMethods(QueryResult result) {
        result.getElements(Init.class, ExecutableElement.class).stream()
                .peek(this::validateNoParameters)
                .forEach(this::processInitMethod);
    }

    private void processInitMethod(ExecutableElement method) {
        TypeElement owner = (TypeElement) method.getEnclosingElement();
        initMethods.computeIfAbsent(owner.getQualifiedName(), name -> new HashSet()).add(method);
    }

    private Predicate<VariableElement> isCollection() {
        return variable -> isCollection(variable);
    }

    private boolean isCollection(VariableElement var) {
        String name = var.asType().toString().replaceAll("<[^\\]]*>", "");
        try {
            return Collection.class.isAssignableFrom(Class.forName(name));
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private boolean isInstanceOf(TypeMirror candidate, TypeElement superType) {
        return isInstanceOf(candidate, superType.asType());
    }

    private boolean isInstanceOf(TypeMirror candidate, TypeMirror superType) {
        return processingEnv.getTypeUtils().isAssignable(candidate, superType);
    }

    private ModuleWriterModel createWriterModel() {
        Map<TypeElement, List<TypeElement>> hierarchy = singletons.stream()
                .collect(Collectors.toMap(Functions.identity(), this::getClassHierarchy));
        SingletonElementMapper mapper = new SingletonElementMapper(hierarchy);
        initMethods.forEach((name, methods) -> mapper.putInitMethods(typeForName(name), methods));
        beanMethods.forEach((name, methods) -> mapper.putBeanMethods(typeForName(name), methods));
        dependencyFields.forEach((name, fields) -> mapper.putDependencyFields(typeForName(name), fields));
        collectionDependencyFields.forEach((name, fields) -> mapper.putCollectionDependencyFields(typeForName(name), fields));
        configFields.forEach((name, fields) -> mapper.putConfigFields(typeForName(name), fields));
        implementations.forEach(mapper::putImplementation);
        singletons.forEach(type -> mapper.putConstructor(type, getConstructor(type)));
        String appClassName = null;
        switch (appClassQualifiedNames.size()) {
            case 0:
                throw new IllegalStateException("No @Application-annotations");
            case 1:
                appClassName = CollectionUtils.getOnlyElement(appClassQualifiedNames);
                break;
            default:
                throw new IllegalStateException("Multiple @Application-annotations");
        }
        return mapper.getSingletonWriterModel(appClassName);
    }

    private TypeElement typeForName(Name name) {
        return processingEnv.getElementUtils().getTypeElement(name.toString());
    }

    @Override
    protected void processingOver() {
        ModuleWriterModel model = createWriterModel();
        writeSingletons(model);
        writeContextFile(model);
    }

    private void writeSingletons(ModuleWriterModel model) {
        ModuleWriter writer = ModuleWriter.builder()
                .model(model)
                .packageName(ModuleFactory.getPackageName(model.getApplicationClass()))
                .simpleName(ModuleFactory.getSimpleName(model.getApplicationClass()))
                .processingEnvironment(processingEnv)
                .build();
        try {
            writer.write();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<TypeElement> getClassHierarchy(TypeElement typeElement) {
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
                .collect(CollectorUtils.toOnlyElement(coll -> new IllegalStateException(typeElement + " has multiple constructors")));
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

    private void processApplication(QueryResult result) {
        appClassQualifiedNames.addAll(result.getElements(Application.class, TypeElement.class).stream()
                .map(TypeElement::getQualifiedName)
                .map(Name::toString).collect(Collectors.toSet()));


    }

    private void writeContextFile(ModuleWriterModel model) {
        IOUtils.write(Collections.singletonList(ModuleFactory.getQualifiedName(model.getApplicationClass())), processingEnv.getFiler(), "META-INF/services/" + ModuleFactory.class.getName());
    }
}
