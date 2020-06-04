package com.jjslinked.processor.codegen.java;

public class MethodInvocationCodeGenerator extends JavaCodeGenerator<MethodInvocationCodeTemplate, MethodInvocationModel> {

    private final ParameterClientIdCodeGenerator parameterClientIdCodeGenerator;

    MethodInvocationCodeGenerator() {
        super(new MethodInvocationCodeTemplate());
        this.parameterClientIdCodeGenerator = new ParameterClientIdCodeGenerator();
    }


}
