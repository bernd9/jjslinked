package com.jjslinked.ast;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class LinkedMethodNode {
    private String clientMethod;
    private List<ParamNode> parameters;
    private String returnType;
    private Set<String> exceptionTypes;
}
