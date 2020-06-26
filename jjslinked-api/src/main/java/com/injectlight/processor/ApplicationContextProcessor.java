package com.injectlight.processor;

import com.google.auto.service.AutoService;
import com.injectlight.Inject;
import com.injectlight.InjectAll;
import com.injectlight.Singleton;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.injectlight.Singleton", "com.injectlight.Inject"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ApplicationContextProcessor extends AbstractProcessor {

    private static final String PACKAGE = "com.injectlight";
    private static final String CONTEXT_SIMPLE_NAME = "ApplicationContext";
    private static final String CONTEXT = PACKAGE + "." + CONTEXT_SIMPLE_NAME;

    private final Set<TypeElement> context = new HashSet<>();
    private final Set<VariableElement> simpleFields = new HashSet<>();
    private final Set<VariableElement> collectionFields = new HashSet<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        context.clear();
        simpleFields.clear();
        collectionFields.clear();
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
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

    private void processInjectAlls(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(InjectAll.class).stream()
                .map(VariableElement.class::cast)
                .forEach(collectionFields::add);
    }

    private void writeContext() {
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(processingEnv.getFiler().createSourceFile(CONTEXT).openOutputStream()))) {
            out.print("package ");
            out.print(PACKAGE);
            out.println(";");
            out.println("import java.util.*;");
            out.println("import java.lang.reflect.*;");
            out.print("public class ");
            out.print(CONTEXT_SIMPLE_NAME);
            out.println(" extends ApplicationContextBase {");
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
                out.print(getGenericType(field).getQualifiedName()); // TODO remove annotations ?
                out.print("\", \"");
                out.print(field.getSimpleName().toString()); // TODO remove annotations ?
                out.print("\", \"");
                out.print(field.asType().toString());
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

    private TypeElement getGenericType(VariableElement collectionVariable) {
        TypeElement collectionClass = (TypeElement) collectionVariable.getEnclosingElement();
        return (TypeElement) collectionClass.getTypeParameters().get(0);
    }


    private void reportError(Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
    }

    private void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
    }
}
