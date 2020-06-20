package com.jjslinked.ast;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ClassNode {
    String qualifiedName;
    String packageName;
    String simpleName;
    Set<AnnotationModel> annotations;
}