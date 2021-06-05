package com.ejc.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.ejc.processor.CallTest"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class CallTestProcessor extends AbstractProcessor {

    private List<TypeElement> elements = new ArrayList<>();
    private List<TypeElement> elements2 = new ArrayList<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        elements.clear();
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            try {
                writeCallTestClasslists();
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "can not write file");
            }
        } else {
            elements.addAll(roundEnv.getElementsAnnotatedWith(CallTest.class).stream()
                    .map(TypeElement.class::cast)
                    .collect(Collectors.toSet()));
            elements2.addAll(roundEnv.getElementsAnnotatedWith(CallTest2.class).stream()
                    .map(TypeElement.class::cast)
                    .collect(Collectors.toSet()));
        }
        return false;
    }

    void writeCallTestClasslists() throws IOException {
        String list = elements.stream().map(TypeElement::getSimpleName).map(Name::toString).collect(Collectors.joining(", "));
        String list2 = elements2.stream().map(TypeElement::getSimpleName).map(Name::toString).collect(Collectors.joining(", "));

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "list: " + list);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "list2: " + list2);

        FileObject fileObject = processingEnv.getFiler().createSourceFile("test.C1", elements.toArray(new Element[elements.size()]));
        try (PrintWriter writer = new PrintWriter(fileObject.openWriter())) {
            writer.println("package test;");
            writer.println("class C1 {");
            writer.println(" // " + list);
            writer.println("}");
        }

        fileObject = processingEnv.getFiler().createSourceFile("test.C2", elements2.toArray(new Element[elements2.size()]));
        try (PrintWriter writer = new PrintWriter(fileObject.openWriter())) {
            writer.println("package test;");
            writer.println("class C2 {");
            writer.println(" // " + list2);
            writer.println("}");
        }
    }

}
