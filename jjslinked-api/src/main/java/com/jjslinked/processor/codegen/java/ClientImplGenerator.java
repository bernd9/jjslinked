package com.jjslinked.processor.codegen.java;

import java.util.List;
import java.util.stream.Collectors;

public class ClientImplGenerator extends JavaCodeGenerator<ClientImplCodeTemplate, ClientImplModel, ClientImplRenderModel> {

    private MethodInvocationCodeGenerator methodInvocationCodeGenerator = new MethodInvocationCodeGenerator();

    ClientImplGenerator() {
        super(new ClientImplCodeTemplate());
    }

    @Override
    ClientImplRenderModel toRenderModel(ClientImplModel model) {
        return new ClientImplRenderModel(model, invocationClasses(model));
    }

    private List<String> invocationClasses(ClientImplModel model) {
        return model.getMethodInvocations().stream()
                .map(methodInvocationCodeGenerator::asString)
                .collect(Collectors.toList());
    }
}
