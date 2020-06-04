package com.jjslinked.processor.codegen.java;


import com.jjslinked.validation.Validator;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Builder
@Getter
public class ParameterPrimitiveOrWrapperModel {
    private String parameterType;
    private String className;
    private Set<Class<? extends Validator>> validators;
    private boolean validateNotNull;
    private String validateRegExpr;
    private String converter;
}
