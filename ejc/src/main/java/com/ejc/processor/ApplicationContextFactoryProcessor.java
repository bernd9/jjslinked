package com.ejc.processor;

import com.ejc.Application;
import com.ejc.InjectAll;
import com.ejc.util.ElementUtils;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.SimpleTypeVisitor9;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.ejc.processor.SingletonLoader", "com.ejc.processor.Injector", "com.ejc.processor.Initializer"})
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

    private String packageName = PACKAGE;

    private PackageElement generatedClassPackage;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        generatedClassPackage = processingEnv.getElementUtils().getPackageElement(PACKAGE);
        loaders.clear();
        injectors.clear();
        multiInjectors.clear();
        collectionFields.clear();
        initializers.clear();
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // TODO Es kommen wohl nur geänderte Dateien an.
        // TODO Das Ergebnis muss in einer Textdate gespeichert und erweitert werden. JSON ? Oder besser Zeilen ?
        try {
            if (!roundEnv.processingOver()) {
                processSingletonLoaders(roundEnv);
                processInjectors(roundEnv);
                processMultiInjectors(roundEnv);
                processInitializers(roundEnv);
            } else {
                writeContext();
            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
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
        List<String> pack = roundEnv.getElementsAnnotatedWith(Application.class).stream()
                .map(TypeElement.class::cast)
                .map(TypeElement::getQualifiedName) // TODO remove Annoations
                .map(ElementUtils::getPackageName)
                .collect(Collectors.toList());

        // TODO Manifestdate mit Mainmethode
        switch (pack.size()) {
            case 0:
                break;
            case 1:
                packageName = pack.get(0);
            default:
                throw new IllegalStateException("Multiple Application-classes");
        }
    }


    private void validateNoParameters(ExecutableElement e) {
        if (e.getParameters() != null && !e.getParameters().isEmpty()) {
            throw new IllegalStateException(e + "  must have no parameters");
        }
    }

    private void processInjectAlls(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(InjectAll.class).stream()
                .map(VariableElement.class::cast)
                .forEach(collectionFields::add);
    }

    private void writeContext() {
        Stream<String> injectorNames = injectors.stream().map(TypeElement::getQualifiedName).map(Name::toString);
        Stream<String> multiInjectorNames = multiInjectors.stream().map(TypeElement::getQualifiedName).map(Name::toString);
        Stream<String> loaderNames = loaders.stream().map(TypeElement::getQualifiedName).map(Name::toString);
        Stream<String> initializerNames = initializers.stream().map(TypeElement::getQualifiedName).map(Name::toString);
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
            injectorNames.map(name -> String.format("    addInjector(%s.class);", name)).forEach(out::println);
            multiInjectorNames.map(name -> String.format("    addMultiInjector(%s.class);", name)).forEach(out::println);
            loaderNames.map(name -> String.format("    addSingletonLoader(%s.class);", name)).forEach(out::println);
            initializerNames.map(name -> String.format("    addInitializer(%s.class);", name)).forEach(out::println);
            out.println(" }");
            out.println("}");
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }

    }

    private String getGenericType(VariableElement collectionVariable) {
        GenericTypeVisitor visitor = new GenericTypeVisitor();
        return collectionVariable.asType().accept(visitor, null).orElseThrow(() -> new IllegalStateException(collectionFields + " must have generic type"));
    }


    private void reportError(Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
    }

    private void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
    }

    private PackageElement findPackage(Element element) {
        Element e = element.getEnclosingElement();
        while (e != null) {
            if (e instanceof PackageElement) {
                return (PackageElement) e;
            }
            e = e.getEnclosingElement();
        }
        throw new IllegalStateException();
    }


    class GenericTypeVisitor extends SimpleTypeVisitor9<Optional<String>, Void> {

        @Override
        public Optional<String> visitDeclared(DeclaredType t, Void aVoid) {
            if (t.getTypeArguments() != null) {
                return t.getTypeArguments().stream().map(Object::toString).findFirst();
            }
            return null;
        }

    }
}
