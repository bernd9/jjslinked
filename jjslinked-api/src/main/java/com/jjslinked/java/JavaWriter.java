package com.jjslinked.java;

import javax.annotation.processing.Filer;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class JavaWriter {

    public void write(ClassElement c, Filer filer) {
        try (PrintWriter out = new PrintWriter(filer.createSourceFile(c.getQualifiedName()).openOutputStream())) {
            write(c, out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(ClassElement c, PrintWriter out) {
        out.print("package ");
        out.print(c.getPackageName());
        out.println(";");

        getImports(c).forEach(imp -> {
            out.print("import ");
            out.print(imp);
            out.println(";");
        });

        out.print("class ");
        out.print(c.getSimpleName());
        out.println(" {");

        if (c.getConstructorElement() != null) {
            writeConstructor(c, out);
        }
        if (c.getFieldElements() != null) {
            c.getFieldElements().forEach(f -> writeField(f, out));
        }

        if (c.getMethods() != null) {
            c.getMethods().forEach(m -> writeMethod(m, out));
        }

        out.println("}");
    }


    private void writeConstructor(ClassElement type, PrintWriter out) {
        ConstructorElement constr = type.getConstructorElement();
        out.print("\t");
        out.print("public ");
        out.print(type.getSimpleName());
        out.print("(");
        out.print(constr.getParameters().stream().map(p -> p.getParameterType().getSimpleName().concat(" ").concat(p.getParameterName())).collect(Collectors.joining(", ")));
        out.println(") {");
        if (constr.getSuperParameters() != null) {
            out.print("\t\t");
            out.print("super(");
            out.print(constr.getParameters().stream().map(ParameterElement::getParameterName).collect(Collectors.joining(", ")));
            out.print(")");
        }
        if (constr.getBody() != null) {
            Arrays.stream(constr.getBody().split("\n")).map(line -> "\t\t" + line).forEach(out::println);
        }
    }

    private void writeField(FieldElement e, PrintWriter out) {
        out.print("\t");
        out.print("private ");
        if (e.isStaticField()) {
            out.print("static ");
        }
        if (e.isFinalField()) {
            out.print("final ");
        }
        out.print(e.getFieldType().getSimpleName());
        out.print(" ");
        out.print(e.getFieldName());
        out.println(";");
    }

    private void writeMethod(MethodElement e, PrintWriter out) {
        out.print("\t");
        out.print("public ");
        out.print(e.getReturnType().getSimpleName());
        out.print(" ");
        out.print(e.getMethodName());
        out.print("(");
        if (e.getParameters() != null) {
            out.print(e.getParameters().stream().map(p -> p.getParameterType().getSimpleName().concat(" ").concat(p.getParameterName())).collect(Collectors.joining(", ")));
        }
        out.println(") {");
        if (e.getBody() != null) {
            Arrays.stream(e.getBody().split("\n")).map(line -> "\t\t" + line).forEach(out::println);
        }
        out.println("}");
    }

    private static Set<String> getImports(ClassElement c) {
        Set<String> imports = new TreeSet<>();
        imports.addAll(c.getFieldElements().stream().map(FieldElement::getFieldType).map(ClassElement::getPackageName).collect(Collectors.toSet()));
        if (c.getConstructorElement() != null) {
            imports.addAll(c.getConstructorElement().getParameters().stream().map(ParameterElement::getParameterType).map(ClassElement::getPackageName).collect(Collectors.toSet()));
        }
        imports.addAll(c.getMethods().stream().map(MethodElement::getReturnType).map(ClassElement::getPackageName).collect(Collectors.toSet()));
        imports.addAll(c.getMethods().stream().map(MethodElement::getParameters).flatMap(List::stream).map(ParameterElement::getParameterType).map(ClassElement::getPackageName).collect(Collectors.toSet()));
        imports.remove(c.getPackageName());
        imports.remove("java.lang");
        return imports;
    }
}
