package com.jjslinked.processor.codegen.java;

import com.jjslinked.validation.ValidationUtil;
import com.jjslinked.validation.Validator;
import lombok.Getter;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
class DefaultParameterCodeModel implements ParameterCodeModel {

    private String parameterType;
    private String className;
    private Set<Class<? extends Validator>> validators;
    private String converter;
    private TypeKind typeKind;

    DefaultParameterCodeModel(VariableElement e) {
        parameterType = e.asType().toString();
        className = JavaCodeGeneratorUtils.providerClassName(e);
        validators = ValidationUtil.validators(e);
        converter = JavaCodeGeneratorUtils.converterClass(e.asType().getKind());
        typeKind = e.asType().getKind();
    }

    boolean isPrimitive() {
        return typeKind.isPrimitive();
    }

    boolean isPrimitiveWrapper() {
        return JavaCodeGeneratorUtils.isPrimitiveWrapper(parameterType);
    }

    boolean isCharSequence() {
        return JavaCodeGeneratorUtils.isCharSequence(parameterType);
    }

    @Override
    public Set<String> getImports() {
        Set<String> set = new HashSet<>(validators.stream().map(Class::getName).collect(Collectors.toSet()));
        set.add(converter);
        return set;
    }
}
