package com.jjslinked.processor.codegen.java;

import lombok.Getter;

import javax.lang.model.element.VariableElement;
import java.util.Set;

import static com.jjslinked.processor.codegen.java.JavaCodeGeneratorUtils.firstToLowerCase;
import static com.jjslinked.processor.codegen.java.JavaCodeGeneratorUtils.providerClassName;

@Getter
public class ParameterModel implements JavaCodeModel {

    private final String name;
    private final String type;

    ParameterModel(VariableElement e) {
        this.name = e.getSimpleName().toString();
        this.type = e.asType().toString();
    }

    public String getVarName() {
        return firstToLowerCase(getSimpleName());
    }

    public String getProviderClassName() {
        return providerClassName(getSimpleName());
    }

    public String getSimpleName() {
        return JavaCodeGeneratorUtils.getSimpleName(type);
    }

    @Override
    public Set<String> getImports() {
        return Set.of(type);
    }
}
