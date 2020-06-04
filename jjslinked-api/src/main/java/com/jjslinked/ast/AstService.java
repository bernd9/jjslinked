package com.jjslinked.ast;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.lang.model.element.TypeElement;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AstService {

    private ConcurrentHashMap<TypeElement, ClientClassNode> nodes = new ConcurrentHashMap<>();
    private static AstService astService = new AstService();

    public ClientClassNode getClassNode(TypeElement e) {
        return nodes.computeIfAbsent(e, AstBuilder::classNode);
    }

    public static AstService getInstance() {
        return astService;
    }

}
