package com.jjslinked.reflect;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MethodParameter {
    private int index;
    private String name;
    private String className;
    private MethodParameterType parameterType;
}
