package com.jjslinked.processor;

import com.google.auto.service.AutoService;
import com.jjslinked.annotations.LinkedMethod;
import com.jjslinked.ast.ClientClassNode;
import com.jjslinked.ast.LinkedMethodNode;
import com.jjslinked.ast.LinkedObservableNode;
import com.jjslinked.ast.ParamNode;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.lang.model.element.ElementKind.PARAMETER;

@SupportedAnnotationTypes("com.jjslinked.annotations.UserId")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class UserIdProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //System.out.println("ClientProcessor: counter=" +ASTProcessor.counter++);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "UserIdProcessor: counter=" +ASTProcessor.counter++);
        annotations.stream().flatMap(annotation -> roundEnv.getElementsAnnotatedWith(annotation).stream())
                .map(VariableElement.class::cast).forEach(this::processVariable);

        return false;
    }

    private void processVariable(VariableElement clientElement) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "UserIdProcessor processing:"+clientElement.getSimpleName());
        

    }

    private Collection<LinkedMethodNode> linkedMethodNodes(TypeElement clientElement) {
        return clientElement.getEnclosedElements().stream()
                .filter(ExecutableElement.class::isInstance)
                .map(ExecutableElement.class::cast)
                .filter(this::isLinkedMethod)
                .map(this::toLinkedMethodNode)
                .collect(Collectors.toSet());
    }

    private boolean isLinkedMethod(ExecutableElement typeElement) {
        return typeElement.getAnnotation(LinkedMethod.class) != null;
    }

    private LinkedMethodNode toLinkedMethodNode(ExecutableElement method) {
        return LinkedMethodNode.builder()
                .clientMethod(getMethodName(method))
                .parameters(methodParameters(method))
                .returnType(method.getReturnType().toString())
                .exceptionTypes(exceptionTypes(method))
                .build();
    }

    private Set<String> exceptionTypes(ExecutableElement method) {
        return method.getThrownTypes().stream().map(TypeMirror::toString).collect(Collectors.toSet());
    }

    private String getMethodName(ExecutableElement method) {
        String nameInAnnotation = method.getAnnotation(LinkedMethod.class).clientMethod();
        return nameInAnnotation.isEmpty() ? method.getSimpleName().toString() : nameInAnnotation;
    }

    private List<ParamNode> methodParameters(ExecutableElement method) {
        return method.getParameters().stream()
                .filter(this::isParameter)
                .map(VariableElement.class::cast)
                .map(this::toParamNode)
                .collect(Collectors.toList());
    }

    private ParamNode toParamNode(VariableElement variable) {
        return ParamNode.builder()
                .name(variable.getSimpleName().toString())
                .type(variable.asType().toString())
                .build();
    }

    private boolean isParameter(Element e) {
        return e.getKind() == PARAMETER;
    }

    private Collection<LinkedObservableNode> linkedObservableNodes(Element clientElement) {
        return null;
    }

}
