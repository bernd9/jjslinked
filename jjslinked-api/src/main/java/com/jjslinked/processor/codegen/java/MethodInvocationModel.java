package com.jjslinked.processor.codegen.java;

import com.jjslinked.annotations.ClientId;
import com.jjslinked.annotations.UserId;
import lombok.Getter;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class MethodInvocationModel implements JavaCodeModel {
    private String invocationClassName;
    private String beanClassName;
    private List<ParameterCodeModel> parameters;

    MethodInvocationModel(ExecutableElement e) {
        this.invocationClassName = JavaCodeGeneratorUtils.invocationClassName(e);
        this.beanClassName = ((TypeElement) e.getEnclosingElement()).getQualifiedName().toString();
        this.parameters = parameters(e);
    }

    private List<ParameterCodeModel> parameters(ExecutableElement e) {
        return e.getParameters().stream().map(this::parameterModel).collect(Collectors.toList());
    }

    @Override
    public Set<String> getImports() {
        Set<String> set = new HashSet<>();
        set.add(beanClassName);
        set.addAll(parameters.stream().map(ParameterCodeModel::getImports).flatMap(Set::stream).collect(Collectors.toSet()));
        return set;
    }

    private ParameterCodeModel parameterModel(VariableElement e) {
        if (e.getAnnotation(ClientId.class) != null) {
            return new ParameterClientIdModel(e);
        }
        if (e.getAnnotation(UserId.class) != null) {
            //return new ParameterClientIdModel(e);
        }
        return new DefaultParameterCodeModel(e);
    }
}
