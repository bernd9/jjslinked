package com.jjslinked.processor.codegen.java;

import com.jjslinked.processor.codegen.JavaSourceUtils;

import javax.lang.model.element.VariableElement;
import java.io.IOException;

public class ParameterClientIdCodeGenerator extends JavaCodeGenerator<ParameterClientIdCodeTemplate, ParameterClientIdModel> {

    ParameterClientIdCodeGenerator() {
        super(new ParameterClientIdCodeTemplate());
    }

    String asString(VariableElement parameter) throws IOException {
        return super.asString(ParameterClientIdModel.builder().providerMethod(providerFunctionName(parameter)).build());
    }

    private static String providerFunctionName(VariableElement variableElement) {
        return "provide" + JavaSourceUtils.firstToUpperCase(variableElement.getSimpleName().toString());
    }
}
