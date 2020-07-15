package com.ejc.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.Processor;
import javax.lang.model.element.ExecutableElement;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;

@AutoService(Processor.class)
public class AdviceClassAnnotationProcessor<A extends Annotation> extends GenericMethodAnnotationProcessor<A> {

    private final Class<InvocationHandler> methodAdviceClass;

    public AdviceClassAnnotationProcessor(Class<A> annotationClass, Class<InvocationHandler> methodAdviceClass) {
        super(annotationClass);
        this.methodAdviceClass = methodAdviceClass;
    }

    @Override
    protected void processMethod(ExecutableElement method, String adviceAnnotation) {
        log("processing %s ", method);
        String packageName = methodAdviceClass.getPackageName();
        String simpleName = "Advice_" + randomString();
        String qualifiedName = packageName + "." + simpleName;
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(processingEnv.getFiler().createSourceFile(qualifiedName).openOutputStream()))) {
            //try (PrintWriter out = new PrintWriter(System.out)) {
            out.print("package ");
            out.print(packageName);
            out.println(";");
            out.println("import com.ejc.processor.*;");
            out.println("import com.ejc.*;");
            out.println("import java.lang.reflect.*;");
            out.println(adviceAnnotation);
            // TODO Statt Vererbung muss das ein Wrapper sein
            out.printf("public class %s extends %s {", simpleName, methodAdviceClass.getName());
            out.println("}");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
