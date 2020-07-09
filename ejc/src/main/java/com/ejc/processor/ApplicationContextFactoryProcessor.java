package com.ejc.processor;

import com.ejc.Application;
import com.ejc.Init;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.ejc.processor.SingletonLoader", "com.ejc.processor.Injector"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ApplicationContextFactoryProcessor extends AbstractProcessor {

    private static final String PACKAGE = "com.ejc.generated";
    private static final String CONTEXT_FACTORY_SIMPLE_NAME = "ApplicationContextFactory";
    private static final String CONTEXT_FACTORY = PACKAGE + "." + CONTEXT_FACTORY_SIMPLE_NAME;

    private final Set<TypeElement> loaders = new HashSet<>();
    private final Set<TypeElement> injectors = new HashSet<>();
    private final Set<VariableElement> collectionFields = new HashSet<>();
    private final Set<ExecutableElement> initializers = new HashSet<>();

    private String packageName = PACKAGE;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        loaders.clear();
        injectors.clear();
        collectionFields.clear();
        initializers.clear();
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // TODO Es kommen wohl nur geänderte Dateien an.
        // TODO Das Ergebnis muss in einer Textdate gespeichert und erweitert werden. JSON ? Oder besser Zeilen ?
        try {
            if (roundEnv.processingOver()) {
                addUnchanged();
                writeContext();
            } else {
                processAnnotations(roundEnv);
            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }

    private void processAnnotations(RoundEnvironment roundEnv) {
        processSingletonLoaders(roundEnv);
        processInjectors(roundEnv);
    }


    private void addUnchanged() {
        addUnchangedLoaders();
        addUnchangedInjectors();
    }


    private void processSingletonLoaders(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(SingletonLoader.class).stream()
                .map(TypeElement.class::cast)
                .forEach(loaders::add);
    }

    private void addUnchangedLoaders() {
        if (loaders.size() > 0) {
            PackageElement packageElement = findPackage(loaders.iterator().next());
            addUnchangedLoaders(packageElement);
        }
    }

    private void addUnchangedLoaders(PackageElement pack) {
        pack.getEnclosedElements().stream()
                .map(TypeElement.class::cast)
                .filter(t -> t.getAnnotation(SingletonLoader.class) != null)
                .forEach(loaders::add);

    }

    private void addUnchangedInjectors() {
        if (injectors.size() > 0) {
            PackageElement packageElement = findPackage(injectors.iterator().next());
            addUnchangedInjectors(packageElement);
        }
    }

    private void addUnchangedInjectors(PackageElement pack) {
        pack.getEnclosedElements().stream()
                .map(TypeElement.class::cast)
                .filter(t -> t.getAnnotation(Injector.class) != null)
                .forEach(injectors::add);

    }


    private void processInjectors(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(Injector.class).stream()
                .map(TypeElement.class::cast)
                .forEach(injectors::add);
    }

    private void processInitializers(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(Init.class).stream()
                .map(ExecutableElement.class::cast)
                .peek(this::validateNoParameters)
                .forEach(initializers::add);
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
        Stream<String> loaderNames = loaders.stream().map(TypeElement::getQualifiedName).map(Name::toString);
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
            out.println("   super();");
            injectorNames.map(name -> String.format("addInjector(\"%s\");\n")).forEach(out::println);
            loaderNames.map(name -> String.format("addLoader(\"%s\");\n")).forEach(out::println);
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
