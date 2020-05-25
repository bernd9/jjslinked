package com.jjslink.processor;

import com.jjslink.js.ast.ast.ClientClassNode;
import com.jjslink.js.ast.ast.LinkedMethodNode;
import com.jjslink.js.ast.ast.LinkedObservableNode;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.Set;

@SupportedAnnotationTypes("com.jjslink.annotations.Client")
public class ClientProcessor extends AbstractProcessor {

    private TypeElement linkedMethodAnnotation;
    private TypeElement linkedObservableAnnotation;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        TypeElements typeElements = new TypeElements(processingEnv);
        this.linkedMethodAnnotation = typeElements.getByName(Annotations.LinkedMethod.className());
        this.linkedMethodAnnotation = typeElements.getByName(Annotations.LinkedObservable.className());


        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        TypeElement clientAnnotation = annotations.stream().findFirst().map(TypeElement.class::cast).orElseThrow();
        roundEnv.getElementsAnnotatedWith(clientAnnotation)
                .parallelStream().map(TypeElement.class::cast).forEach(this::processClientClass);
        return false;
    }

    private void processClientClass(TypeElement clientElement) {
        ClientClassNode clientClassNode = ClientClassNode.builder()
                .linkedObservables(linkedObservableNodes(clientElement))
                .linkedMethods(linkedMethodNodes(clientElement))
                .build();

    }

    private Collection<LinkedMethodNode> linkedMethodNodes(TypeElement clientElement) {
        clientElement.getEnclosedElements();
        return null;
    }

    private Collection<LinkedObservableNode> linkedObservableNodes(Element clientElement) {
        return null;
    }

    private TypeElement getTypeElement(String typeName) {
        return this.processingEnv.getElementUtils().getTypeElement(typeName);
    }
}
