package com.jjslink.js.ast.ast;

import lombok.Builder;

import java.util.Collection;

@Builder
public class ClientClassNode {

    private Collection<LinkedMethodNode> linkedMethods;
    private Collection<LinkedObservableNode> linkedObservables;

}
