package com.ejc.processor;

import com.ejc.*;
import com.ejc.util.IOUtils;
import com.ejc.util.ReflectionUtils;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.ejc.processor.SingletonLoader", "com.ejc.processor.Injector", "com.ejc.processor.Initializer", "com.ejc.processor.SystemPropertyInjector", "com.ejc.Application"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ApplicationContextFactoryProcessor extends AbstractProcessor {

    private static final String PACKAGE = "com.ejc.generated";
    private static final String CONTEXT_FACTORY_SIMPLE_NAME = "ApplicationContextFactory";

    private Set<ExecutableElement> initMethods = new HashSet<>();
    private Set<VariableElement> singleValueDependencies = new HashSet<>();
    private Set<VariableElement> multiValueDependencies = new HashSet<>();
    private Set<TypeElement> singletons = new HashSet<>();

    private String packageName = PACKAGE;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Stream.of(Singleton.class, Inject.class, InjectAll.class, Init.class).map(Class::getName).collect(Collectors.toSet());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        initMethods.clear();
        singleValueDependencies.clear();
        multiValueDependencies.clear();
        singletons.clear();
        super.init(processingEnv);
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (!roundEnv.processingOver()) {
                processInitMethods(roundEnv);
                processSingleValueDependencies(roundEnv);
                processMultiValueDependencies(roundEnv);
                processSingletons(roundEnv);
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

    private void processSingletons(RoundEnvironment roundEnv) {
        singletons.addAll(roundEnv.getElementsAnnotatedWith(Singleton.class).stream()
                .map(TypeElement.class::cast)
                .collect(Collectors.toSet()));
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
        return packageName + "." + CONTEXT_FACTORY_SIMPLE_NAME;
    }

    private void writeApplicationContextFactory() {
        /*
        Stream<String> injectorNames = injectors.stream().map(TypeElement::getQualifiedName).map(Name::toString);
        Stream<String> multiInjectorNames = multiInjectors.stream().map(TypeElement::getQualifiedName).map(Name::toString);
        Stream<String> loaderNames = loaders.stream().map(TypeElement::getQualifiedName).map(Name::toString);
        Stream<String> implLoaderNames = implLoaders.stream().map(TypeElement::getQualifiedName).map(Name::toString);
        Stream<String> initializerNames = initializers.stream().map(TypeElement::getQualifiedName).map(Name::toString);
        Stream<String> propertyInjectorNames = propertyInjectors.stream().map(TypeElement::getQualifiedName).map(Name::toString);
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(processingEnv.getFiler().createSourceFile(factoryQualifiedName()).openOutputStream()))) {
            out.print("package ");
            out.print(packageName);
            out.println(";");
            out.println("import java.util.*;");
            out.println("import java.lang.reflect.*;");
            out.print("public class ");
            out.print(CONTEXT_FACTORY_SIMPLE_NAME);
            out.print(" extends ");
            out.print(ApplicationContextFactoryBase.class.getName());
            out.println(" {");
            out.print("   public ");
            out.print(CONTEXT_FACTORY_SIMPLE_NAME);
            out.println("() {");
            out.println("    super();");
            injectorNames.map(name -> String.format("    addInjector(%s.class);", name)).forEach(out::println);
            multiInjectorNames.map(name -> String.format("    addMultiInjector(%s.class);", name)).forEach(out::println);
            loaderNames.map(name -> String.format("    addSingletonLoader(%s.class);", name)).forEach(out::println);
            implLoaderNames.map(name -> String.format("    addImplementationLoader(%s.class);", name)).forEach(out::println);
            initializerNames.map(name -> String.format("    addInitializer(%s.class);", name)).forEach(out::println);
            propertyInjectorNames.map(name -> String.format("    addPropertyInjector(%s.class);", name)).forEach(out::println);
            out.println(" }");
            out.println("}");
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }

         */

    }

    private void writeContextFile() {
        IOUtils.write(Collections.singletonList(factoryQualifiedName()), processingEnv.getFiler(), "META-INF/services/" + ApplicationContextFactory.class.getName());
    }

    private void reportError(Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
    }

    private void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
    }

}
