package com.jjslinked.ast;

import lombok.Builder;
import lombok.Value;

import javax.lang.model.element.Name;
import javax.lang.model.type.TypeKind;

@Value
@Builder
public class ParameterNode {
    private final String type;
    private final TypeKind typeKind;
    private final Name name;
    private final boolean clientId;
    private final boolean userId;
    private String parameterProviderType;
    private final boolean string;
    private final boolean primitiveOrWrapper;
}
