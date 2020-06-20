package com.jjslinked.java;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConstructorElement {

    @Singular
    private List<ParameterElement> parameters;
    @Singular
    private List<ParameterElement> superParameters;

    private String body;

}
