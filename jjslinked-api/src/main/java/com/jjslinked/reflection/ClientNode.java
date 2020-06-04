package com.jjslinked.reflection;

import com.jjslinked.annotations.Client;
import lombok.Builder;

import javax.lang.model.element.TypeElement;
import java.util.List;

import static com.jjslinked.reflection.ReflectionUtils.packageName;
import static com.jjslinked.reflection.ReflectionUtils.simpleName;

@Builder
public class ClientNode {

    private String packageName;
    private String simpleName;
    private String qualifier;
    private List<LinkedMethodNode> linkedMethods;


    static ClientNode build(TypeElement e) {
        String qualifiedName = e.getQualifiedName().toString();
        return ClientNode.builder()
                .packageName(packageName(qualifiedName))
                .simpleName(simpleName(qualifiedName))
                .qualifier(e.getAnnotation(Client.class).value())
                .linkedMethods(LinkedMethodNode.linkedMethods(e))
                .build();
    }
}
