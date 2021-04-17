package one.xis.sql.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
class MethodModel {
    private final String name;
    private TypeName returnType;
    private List<ParameterModel> parameters = new ArrayList<>();
    private Modifier[] modifiers = new Modifier[0];
    private Set<Class<? extends Annotation>> annotations = new HashSet<>();
    private Set<AnnotationSpec> annotationSpecs = new HashSet<>();
    private List<TypeVariable> typeVariables = new ArrayList<>();

    @Getter(AccessLevel.PRIVATE)
    private final MethodSpec.Builder methodBuilder;

    MethodModel(String name) {
        this.name = name;
        this.methodBuilder = MethodSpec.methodBuilder(name);
    }

    MethodModel(String name, boolean override) {
        this(name);
        if (override) {
            addAnnotation(Override.class);
        }
    }

    ParameterModel addParameter(TypeName type, String name) {
        ParameterModel parameterModel = new ParameterModel(type, name);
        parameters.add(parameterModel);
        return parameterModel;
    }

    void setModifiers(Modifier... m) {
        modifiers = m;
    }

    void addAnnotation(Class<? extends Annotation> annotationClass) {
        annotations.add(annotationClass);
    }

    void addAnnotation(AnnotationSpec annotationSpec) {
        annotationSpecs.add(annotationSpec);
    }

    TypeVariable addTypeVariable(String name, Class<?> bounds) {
        TypeVariable typeVariable = new TypeVariable(name, bounds);
        typeVariables.add(typeVariable);
        return typeVariable;
    }

    TypeVariable addTypeVariable(String name, TypeName bounds) {
        TypeVariable typeVariable = new TypeVariable(name, bounds);
        typeVariables.add(typeVariable);
        return typeVariable;
    }

    void addStatement(String s, Object... args) {
        methodBuilder.addStatement(s, args);
    }

    MethodSpec.Builder javaBuilder() {
        methodBuilder.addModifiers(modifiers);
        methodBuilder.returns(returnType);
        parameters.forEach(parameterModel -> methodBuilder.addParameter(parameterModel.getTypeName(), parameterModel.getName()));
        annotations.forEach(methodBuilder::addAnnotation);
        annotationSpecs.forEach(methodBuilder::addAnnotation);
        typeVariables.stream().map(TypeVariable::toTypeVariableName).forEach(methodBuilder::addTypeVariable);
        return methodBuilder;
    }
}
