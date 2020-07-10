package com.ejc.processor;

import com.ejc.Application;
import com.ejc.util.ElementUtils;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
    private static final String CONTEXT_FACTORY = PACKAGE + "." + CONTEXT_FACTORY_SIMPLE_NAME;

    private final Set<TypeElement> loaders = new HashSet<>();
    private final Set<TypeElement> injectors = new HashSet<>();
    private final Set<TypeElement> multiInjectors = new HashSet<>();
    private final Set<VariableElement> collectionFields = new HashSet<>();
    private final Set<TypeElement> initializers = new HashSet<>();
    private final Set<TypeElement> propertyInjectors = new HashSet<>();
    private final List<Class<?>> includeApps = new ArrayList<>();

    private String packageName = PACKAGE;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        loaders.clear();
        injectors.clear();
        multiInjectors.clear();
        collectionFields.clear();
        initializers.clear();
        includeApps.clear();
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (!roundEnv.processingOver()) {
                processSingletonLoaders(roundEnv);
                processPropertyInjectors(roundEnv);
                processInjectors(roundEnv);
                processMultiInjectors(roundEnv);
                processInitializers(roundEnv);
                processApplication(roundEnv);
            } else {
                writeApplicationContextFactory();
            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }

    private void processPropertyInjectors(RoundEnvironment roundEnvironment) {
        getGeneratedClassPackage(SystemPropertyInjector.class, roundEnvironment)
                .map(PackageElement::getEnclosedElements)
                .orElse(Collections.emptyList()).stream()
                .filter(TypeElement.class::isInstance)
                .map(TypeElement.class::cast)
                .filter(t -> t.getAnnotation(SystemPropertyInjector.class) != null)
                .forEach(propertyInjectors::add);
    }

    private void processSingletonLoaders(RoundEnvironment roundEnvironment) {
        getGeneratedClassPackage(SingletonLoader.class, roundEnvironment)
                .map(PackageElement::getEnclosedElements)
                .orElse(Collections.emptyList()).stream()
                .filter(TypeElement.class::isInstance)
                .map(TypeElement.class::cast)
                .filter(t -> t.getAnnotation(SingletonLoader.class) != null)
                .forEach(loaders::add);
    }


    private void processInjectors(RoundEnvironment roundEnvironment) {
        getGeneratedClassPackage(Injector.class, roundEnvironment)
                .map(PackageElement::getEnclosedElements)
                .orElse(Collections.emptyList()).stream()
                .filter(TypeElement.class::isInstance)
                .map(TypeElement.class::cast)
                .filter(t -> t.getAnnotation(Injector.class) != null)
                .forEach(injectors::add);
    }


    private void processMultiInjectors(RoundEnvironment roundEnvironment) {
        getGeneratedClassPackage(MultiInjector.class, roundEnvironment)
                .map(PackageElement::getEnclosedElements)
                .orElse(Collections.emptyList()).stream()
                .filter(TypeElement.class::isInstance)
                .map(TypeElement.class::cast)
                .filter(t -> t.getAnnotation(MultiInjector.class) != null)
                .forEach(multiInjectors::add);
    }


    private void processInitializers(RoundEnvironment roundEnvironment) {
        getGeneratedClassPackage(Initializer.class, roundEnvironment)
                .map(PackageElement::getEnclosedElements)
                .orElse(Collections.emptyList()).stream()
                .filter(TypeElement.class::isInstance)
                .map(TypeElement.class::cast)
                .filter(t -> t.getAnnotation(Initializer.class) != null)
                .forEach(initializers::add);
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
// TODO Manifestdate mit Mainmethode
        TypeElement appClass = null;
        switch (classes.size()) {
            case 0:
                return;
            case 1:
                appClass = classes.get(0);
                break;
            default:
                throw new IllegalStateException("Multiple Application-classes");
        }

        packageName = ElementUtils.getPackageName(appClass.getQualifiedName());
        includeApps.addAll(Arrays.asList(appClass.getAnnotation(Application.class).include()));

    }

    private void writeApplicationContextFactory() {
        Stream<String> injectorNames = injectors.stream().map(TypeElement::getQualifiedName).map(Name::toString);
        Stream<String> multiInjectorNames = multiInjectors.stream().map(TypeElement::getQualifiedName).map(Name::toString);
        Stream<String> loaderNames = loaders.stream().map(TypeElement::getQualifiedName).map(Name::toString);
        Stream<String> initializerNames = initializers.stream().map(TypeElement::getQualifiedName).map(Name::toString);
        Stream<String> propertyInjectorNames = propertyInjectors.stream().map(TypeElement::getQualifiedName).map(Name::toString);
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(processingEnv.getFiler().createSourceFile(CONTEXT_FACTORY).openOutputStream()))) {
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
            //includeApps.stream().map(Class::getName).map(name -> String.format("    addApplication(%s.class);", name)).forEach(out::println);
            injectorNames.map(name -> String.format("    addInjector(%s.class);", name)).forEach(out::println);
            multiInjectorNames.map(name -> String.format("    addMultiInjector(%s.class);", name)).forEach(out::println);
            loaderNames.map(name -> String.format("    addSingletonLoader(%s.class);", name)).forEach(out::println);
            initializerNames.map(name -> String.format("    addInitializer(%s.class);", name)).forEach(out::println);
            propertyInjectorNames.map(name -> String.format("    addPropertyInjector(%s.class);", name)).forEach(out::println);
            out.println(" }");
            out.println("}");
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void reportError(Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
    }

    private void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
    }

}
