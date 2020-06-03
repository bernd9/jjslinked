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

    public List<MethodNode> getImplementedMethods() {
        return getMethods().stream().filter(MethodNode::isImplemented).collect(Collectors.toList());
    }

    public List<String> getImplementedMethodNames() {
        return getMethods().stream().filter(MethodNode::isImplemented).map(MethodNode::getName).collect(Collectors.toList());
    }

    public List<MethodNode> getAbstractMethods() {
        return getMethods().stream().filter(MethodNode::isAbstractMethod).collect(Collectors.toList());
    }

    public List<String> getAbstractMethodNames() {
        return getMethods().stream().filter(MethodNode::isAbstractMethod).map(MethodNode::getName).collect(Collectors.toList());
    }
}
