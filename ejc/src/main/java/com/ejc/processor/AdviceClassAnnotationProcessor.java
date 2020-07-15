package com.ejc.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.Processor;
import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.util.Collection;

@AutoService(Processor.class)
public class AdviceClassAnnotationProcessor<A extends Annotation> extends GenericMethodAnnotationProcessor<A> {

    private final Class<InvocationHandler> methodAdviceClass;

    public AdviceClassAnnotationProcessor(Class<A> annotationClass, Class<InvocationHandler> methodAdviceClass) {
        super(annotationClass);
        this.methodAdviceClass = methodAdviceClass;
    }

    @Override
    protected void processMethods(Collection<ExecutableElement> methods) throws Exception {
        AdviceWriter adviceWriter = new AdviceWriter(getAnnotationClass(), methodAdviceClass, methods, processingEnv);
        adviceWriter.write();
    }
}
