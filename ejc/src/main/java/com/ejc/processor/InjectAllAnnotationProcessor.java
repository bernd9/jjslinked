package com.ejc.processor;

import com.ejc.InjectAll;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.SimpleTypeVisitor9;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.ejc.InjectAll"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class InjectAllAnnotationProcessor extends AbstractProcessor {

    private static final String PACKAGE = "com.ejc.generated.multiinject";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (!roundEnv.processingOver()) {
                processInjects(roundEnv);
            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }

    private void processInjects(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(InjectAll.class).stream()
                .map(VariableElement.class::cast)
                .peek(this::validateType)
                .forEach(this::writeInjector);
    }

    private void validateType(VariableElement element) {
        // TODO must be a Set
    }


    private void writeInjector(VariableElement e) {
        log("processing %s", e);
        String injectorSimpleName = "MultiInjector_" + randomString();
        String injectorQualifiedName = PACKAGE + "." + injectorSimpleName;
        String fieldDeclaringClass = ((TypeElement) e.getEnclosingElement()).getQualifiedName().toString();
        String fieldType = e.asType().toString();
        String fieldName = e.getSimpleName().toString();
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(processingEnv.getFiler().createSourceFile(injectorQualifiedName).openOutputStream()))) {
            out.print("package ");
            out.print(PACKAGE);
            out.println(";");
            out.println("import com.ejc.processor.*;");
            out.println("import java.lang.reflect.*;");
            out.println("@MultiInjector");
            out.print("public class ");
            out.print(injectorSimpleName);
            out.print(" extends ");
            out.print(MultiInjectorBase.class.getName());
            out.println(" {");
            out.print(" public ");
            out.print(injectorSimpleName);
            out.println("() {");
            out.print("   super(");
            out.print("\"");
            out.print(fieldDeclaringClass);
            out.print("\"");
            out.print(", ");
            out.print("\"");
            out.print(fieldName);
            out.print("\"");
            out.print(", ");
            out.print("\"");
            out.print(getGenericType(e));
            out.print("\"");
            out.println(");");
            out.println("  }");
            out.println(" }");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String randomString() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private void reportError(Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
    }

    private void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
    }

    private String getGenericType(VariableElement collectionVariable) {
        GenericTypeVisitor visitor = new GenericTypeVisitor();
        return collectionVariable.asType().accept(visitor, null).orElseThrow(() -> new IllegalStateException(collectionVariable + " must have generic type"));
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
