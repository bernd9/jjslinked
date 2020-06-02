package com.jjslinked.ast;

import lombok.Builder;
import lombok.Value;

import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<MethodNode> getAbstractMethods() {
        return getMethods().stream().filter(MethodNode::isAbstractMethod).collect(Collectors.toList());
    }
}
