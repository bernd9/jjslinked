package com.ejc.processor;

import com.ejc.*;
import com.ejc.util.ElementUtils;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
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

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.ejc.Singleton", "com.ejc.Inject", "com.ejc.Init", "com.ejc.Application"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ApplicationContextProcessor extends AbstractProcessor {

    private static final String PACKAGE = "com.ejc";
    private static final String CONTEXT_SIMPLE_NAME = "ApplicationContext";
    private static final String CONTEXT = PACKAGE + "." + CONTEXT_SIMPLE_NAME;

    private final Set<TypeElement> context = new HashSet<>();
    private final Set<VariableElement> simpleFields = new HashSet<>();
    private final Set<VariableElement> collectionFields = new HashSet<>();
    private final Set<ExecutableElement> initializers = new HashSet<>();

    private String packageName = PACKAGE;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        context.clear();
        simpleFields.clear();
        collectionFields.clear();
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // TODO Es kommen wohl nur geänderte Dateien an.
        // TODO Das Ergebnis muss in einer Textdate gespeichert und erweitert werden. JSON ? Oder besser Zeilen ?
        try {
            if (roundEnv.processingOver()) {
                writeContext();
            } else {
                addContextData(roundEnv);
            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }

    private void addContextData(RoundEnvironment roundEnv) {
        processSingletons(roundEnv);
        processInjects(roundEnv);
        processInjectAlls(roundEnv);
        processInitializers(roundEnv);
        processApplication(roundEnv);
    }


    private void processSingletons(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(Singleton.class).stream()
                .map(TypeElement.class::cast)
                .forEach(context::add);
    }

    private void processInjects(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(Inject.class).stream()
                .map(VariableElement.class::cast)
                .forEach(simpleFields::add);
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
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(processingEnv.getFiler().createSourceFile(CONTEXT).openOutputStream()))) {
            out.print("package ");
            out.print(packageName);
            out.println(";");
            out.println("import java.util.*;");
            out.println("import java.lang.reflect.*;");
            out.print("public class ");
            out.print(CONTEXT_SIMPLE_NAME);
            out.print(" extends ");
            out.print(ApplicationContextBase.class.getName());
            out.println(" {");
            out.print("   public ");
            out.print(CONTEXT_SIMPLE_NAME);
            out.println("() {");
            out.println("   super();");
            context.stream()
                    .map(TypeElement::getQualifiedName)
                    .map(Name::toString)
                    .forEach(className -> {
                        out.print("   add(\"");
                        out.print(className);
                        out.println("\");");
                    });

            simpleFields.forEach(field -> {
                out.print("   inject(\"");
                out.print(((TypeElement) field.getEnclosingElement()).getQualifiedName()); // TODO remove annotations ?
                out.print("\", \"");
                out.print(field.getSimpleName().toString()); // TODO remove annotations ?
                out.print("\", \"");
                out.print(field.asType().toString());
                out.println("\");");
            });
            collectionFields.forEach(field -> {
                out.print("   injectAll(\"");
                out.print(((TypeElement) field.getEnclosingElement()).getQualifiedName()); // TODO remove annotations ?
                out.print("\", \"");
                out.print(field.getSimpleName().toString()); // TODO remove annotations ?
                out.print("\", \"");
                out.print(getGenericType(field));
                out.println("\");");
            });
            initializers.forEach(method -> {
                out.print("   invokeInitMethod(\"");
                out.print(((TypeElement) method.getEnclosingElement()).getQualifiedName()); // TODO remove annotations ?
                out.print("\", \"");
                out.print(method.getSimpleName());
                out.println("\");");
            });
            out.println(" }");
            out.print(" private static ");
            out.print(CONTEXT_SIMPLE_NAME);
            out.print(" INSTANCE = new ");
            out.print(CONTEXT_SIMPLE_NAME);
            out.println("();");
            out.print(" public static ");
            out.print(CONTEXT_SIMPLE_NAME);
            out.println(" getInstance() {");
            out.println("  return INSTANCE;");
            out.println(" }");

            out.println("}");

        } catch (IOException e) {
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
