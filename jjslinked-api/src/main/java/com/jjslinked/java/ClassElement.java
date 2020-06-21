package com.jjslinked.java;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ClassElement {

    String simpleName;
    String packageName;

    String staticInitializer;

    @Singular
    Set<FieldElement> fieldElements;
    ConstructorElement constructorElement;

    Class<?> superClass;

    @Singular
    List<MethodElement> methods;

    @Singular
    List<AnnotationElement> annotations;

    ClassElement parentClass;

    public String getQualifiedName() {
        return packageName + "." + simpleName;
    }


}
