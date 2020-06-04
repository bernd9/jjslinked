package com.jjslinked.processor.codegen.java;

import javax.lang.model.element.ExecutableElement;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SignatureModel implements JavaCodeModel {
    private List<ParameterModel> parameters;

    SignatureModel(ExecutableElement e) {
        this.parameters = e.getParameters().stream().map(ParameterModel::new).collect(Collectors.toList());
    }

    @Override
    public Set<String> getImports() {
        return parameters.stream().map(ParameterModel::getImports).flatMap(Collection::stream).collect(Collectors.toSet());
    }
}
