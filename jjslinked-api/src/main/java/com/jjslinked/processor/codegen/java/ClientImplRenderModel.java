package com.jjslinked.processor.codegen.java;

import lombok.Getter;
import lombok.experimental.Delegate;


public class ClientImplRenderModel {

    @Delegate
    private final ClientImplModel model;

    @Getter
    private final Iterable<String> invocationClasses;

    public ClientImplRenderModel(ClientImplModel model, Iterable<String> invocationClasses) {
        this.model = model;
        this.invocationClasses = invocationClasses;
    }


}
