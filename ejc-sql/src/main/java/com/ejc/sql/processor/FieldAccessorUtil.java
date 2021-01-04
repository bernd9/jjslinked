package com.ejc.sql.processor;

import com.ejc.util.CollectorUtils;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

// TODO may better move to somewhere else ?
@RequiredArgsConstructor
class FieldAccessorUtil {
    private final ProcessingEnvironment processingEnvironment;

    Optional<ExecutableElement> getGetter(VariableElement field) {
        return getOwner(field).getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .map(ExecutableElement.class::cast)
                .filter(e -> isGetterForField(e, field))
                .collect(CollectorUtils.toOnlyOptional());
    }

    // TODO : may be this is to slow. Iteration over all methods for wach field.
    // TODO : replace with type-reflection instance
    Optional<ExecutableElement> getSetter(VariableElement field) {
        return getOwner(field).getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .map(ExecutableElement.class::cast)
                .filter(e -> isSetterForField(e, field))
                .collect(CollectorUtils.toOnlyOptional());
    }

    private boolean isSetterForField(ExecutableElement setter, VariableElement field) {
        if (setter.getParameters().size() != 1) {
            return false;
        }
        if (!matchesFieldType(setter.getParameters().get(0), field)) {
            return false;
        }
        if (!getSetterName(field).equals(setter.getSimpleName().toString())) {
            return false;
        }
        return true;
    }

    private boolean isGetterForField(ExecutableElement getter, VariableElement field) {
        if (!getter.getParameters().isEmpty()) {
            return false;
        }
        if (!matchesFieldType(getter.getReturnType(), field.asType())) {
            return false;
        }
        if (!getGetterName(field).equals(getter.getSimpleName().toString())) {
            return false;
        }
        return true;
    }

    private boolean matchesFieldType(VariableElement parameter, VariableElement field) {
        return processingEnvironment.getTypeUtils().isAssignable(parameter.asType(), field.asType());
    }

    private boolean matchesFieldType(TypeMirror parameterType, TypeMirror fieldType) {
        return processingEnvironment.getTypeUtils().isAssignable(parameterType, fieldType);
    }

    private TypeElement getOwner(VariableElement field) {
        return (TypeElement) field.getEnclosingElement();
    }

    private String getGetterName(VariableElement field) {
        return getGetterName(field.getSimpleName().toString());
    }

    private String getGetterName(String fieldName) {
        return new StringBuilder("get")
                .append(NamingUtil.firstToUpperCase(fieldName))
                .toString();
    }

    private String getSetterName(VariableElement field) {
        return getSetterName(field.getSimpleName().toString());
    }

    private String getSetterName(String fieldName) {
        return new StringBuilder("set")
                .append(NamingUtil.firstToUpperCase(fieldName))
                .toString();
    }

}
