package com.jjslinked.ast;

import lombok.Builder;
import lombok.Value;

import javax.lang.model.element.TypeElement;
import java.util.List;

@Value
@Builder
public class ClassNode {
    private String packageName;
    private String qualifiedName;
    private String simpleName;
    private String qualifier;
    private String instanceName;
    private List<MethodNode> methods;
    private TypeElement typeElement;
}
