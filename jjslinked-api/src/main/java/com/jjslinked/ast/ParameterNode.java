package com.jjslinked.ast;

import com.jjslinked.parameters.ParameterProvider;
import lombok.Builder;
import lombok.Value;

import javax.lang.model.element.Name;
import javax.lang.model.type.TypeKind;

@Value
@Builder
public class ParameterNode {
    private final String type;
    private final TypeKind typeKind;
    private final Name parameterName;
    private final boolean clientId;
    private final boolean userId;
    private ParameterProvider parameterProvider;
}
