package com.ejc.processor;

import com.ejc.*;
import com.ejc.api.context.UndefinedClass;
import com.ejc.api.context.model.Singletons;
import com.ejc.util.ClassUtils;
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

import static com.ejc.util.JavaModelUtils.*;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class SingletonProcessor extends ProcessorBase {

    private Map<Name, Collection<ExecutableElement>> initMethods = new HashMap<>();
    private Map<Name, Collection<ExecutableElement>> beanMethods = new HashMap<>();
    private Map<Name, Collection<VariableElement>> dependencyFields = new HashMap<>();
    private Map<Name, Collection<VariableElement>> collectionDependencyFields = new HashMap<>();
    private Map<Name, Collection<VariableElement>> configFields = new HashMap<>();
    private Map<Name, List<ConstructorParameterElement>> constructorParameters = new HashMap<>();
    private Set<TypeElement> singletons = new HashSet<>();
    private Map<TypeElement, TypeElement> implementations = new HashMap<>();

    private String appClassQualifiedName;

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
                .peek(this::processConstructorParameters)
                .collect(Collectors.toSet()));
    }

    private void processConstructorParameters(TypeElement singleton) {
        Name owner = singleton.getQualifiedName();
        List<ConstructorParameterElement> parameters = constructorParameters.computeIfAbsent(owner, name -> new ArrayList<>());
        getConstructor(singleton).getParameters().forEach(parameter -> {
            if (isCollection(parameter)) {
                parameters.add(new CollectionConstructorParameterElement(getCollectionType(parameter), getGenericType(parameter)));
            } else {
                parameters.add(new SimpleConstructorParameterElement(parameter.asType()));
            }
        });
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
        Set<VariableElement> fields = result.getElements(Inject.class, VariableElement.class);
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
                .peek(this::validateNoParameters)
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

    private Class<? extends Collection> getCollectionType(VariableElement parameter) {
        return (Class<? extends Collection>) ClassUtils.classForName(parameter.asType().toString().replaceAll("<[^\\]]*>", ""));
    }

    private boolean isInstanceOf(TypeMirror candidate, TypeElement superType) {
        return isInstanceOf(candidate, superType.asType());
    }

    private boolean isInstanceOf(TypeMirror candidate, TypeMirror superType) {
        return processingEnv.getTypeUtils().isAssignable(candidate, superType);
    }


    private SingletonWriterModel createWriterModel() {
        Map<TypeElement, List<TypeElement>> hierarchy = singletons.stream()
                .collect(Collectors.toMap(Functions.identity(), this::getClassHierarchy));
        SingletonWriterModelBuilder modelBuilder = new SingletonWriterModelBuilder(hierarchy);
        initMethods.forEach((name, methods) -> modelBuilder.putInitMethods(typeForName(name), methods));
        beanMethods.forEach((name, methods) -> modelBuilder.putBeanMethods(typeForName(name), methods));
        dependencyFields.forEach((name, fields) -> modelBuilder.putDependencyFields(typeForName(name), fields));
        collectionDependencyFields.forEach((name, fields) -> modelBuilder.putCollectionDependencyFields(typeForName(name), fields));
        configFields.forEach((name, fields) -> modelBuilder.putConfigFields(typeForName(name), fields));
        implementations.forEach(modelBuilder::putImplementation);
        constructorParameters.forEach((name, parameters) -> modelBuilder.putConstructorParameters(typeForName(name), parameters));
        return modelBuilder.getSingletonWriterModel();
    }

    private TypeElement typeForName(Name name) {
        return processingEnv.getElementUtils().getTypeElement(name.toString());
    }

    @Override
    protected void processingOver() {
        writeSingletons(createWriterModel());
        writeContextFile();
    }

    private void writeSingletons(SingletonWriterModel model) {
        SingletonsWriter writer = SingletonsWriter.builder()
                .model(model)
                .packageName(Singletons.getPackageName(appClassQualifiedName))
                .simpleName(Singletons.getSimpleName(appClassQualifiedName))
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

    private static List<TypeMirror> getConstructorParameters(TypeElement typeElement) {
        return getConstructor(typeElement).getParameters().stream().map(VariableElement::asType).collect(Collectors.toList());
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

    private void processApplication(QueryResult result) {
        Set<TypeElement> classes = result.getElements(Application.class, TypeElement.class);
        TypeElement appClass;
        switch (classes.size()) {
            case 0:
                return;
            case 1:
                appClass = classes.iterator().next();
                break;
            default:
                throw new IllegalStateException("Multiple Application-annotations");
        }
        appClassQualifiedName = appClass.getQualifiedName().toString();
    }

    private String factoryQualifiedName() {
        return appClassQualifiedName + "." + ApplicationContextFactory.IMPLEMENTATION_SIMPLE_NAME;
    }

    private void writeApplicationContextFactory() throws IOException {

    }


    private void writeContextFile() {
        IOUtils.write(Collections.singletonList(factoryQualifiedName()), processingEnv.getFiler(), "META-INF/services/" + Singletons.class.getName());
    }


}
