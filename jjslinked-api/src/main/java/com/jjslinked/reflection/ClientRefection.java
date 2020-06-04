package com.jjslinked.reflection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.lang.model.element.TypeElement;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClientRefection {

    private static ClientRefection clientReflection = new ClientRefection();

    private ConcurrentHashMap<TypeElement, ClientNode> nodes = new ConcurrentHashMap<>();

    public ClientNode getClassNode(TypeElement e) {
        return nodes.computeIfAbsent(e, ClientNode::build);
    }

    public static ClientRefection getInstance() {
        return clientReflection;
    }
}
