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

    @Singular
    Set<FieldElement> fieldElements;
    ConstructorElement constructorElement;

    @Singular
    List<MethodElement> methods;

    ClassElement parentClass;


}
