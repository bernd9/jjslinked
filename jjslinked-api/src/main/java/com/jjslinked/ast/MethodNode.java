package com.jjslinked.ast;

import lombok.Builder;
import lombok.Value;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import java.util.List;

@Builder
@Value
public class MethodNode {
    private String name;
    private TypeKind returnTypeKind;
    private String returnType;
    private List<ParameterNode> parameterNodes;
    private ExecutableElement executableElement;
    private boolean abstractMethod;
    private InvokationType invokationType;
    private String qualifier;
    private String proxyName;
}
