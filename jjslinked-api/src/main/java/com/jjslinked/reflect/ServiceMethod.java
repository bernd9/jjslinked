package com.jjslinked.reflect;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class ServiceMethod {
    private String name;

    @Singular
    private List<MethodParameter> parameters;
}
