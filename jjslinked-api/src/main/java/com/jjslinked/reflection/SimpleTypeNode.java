package com.jjslinked.reflection;

import lombok.Builder;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static com.jjslinked.reflection.ReflectionUtils.packageName;
import static com.jjslinked.reflection.ReflectionUtils.simpleName;

@Builder
public class SimpleTypeNode {

    private String packageName;
    private String simpleName;
    private TypeKind typeKind;

    static SimpleTypeNode create(TypeMirror typeMirror) {
        String qualifiedName = typeMirror.toString();
        return SimpleTypeNode.builder()
                .packageName(packageName(qualifiedName))
                .simpleName(simpleName(qualifiedName))
                .typeKind(typeMirror.getKind())
                .build();
    }
}
