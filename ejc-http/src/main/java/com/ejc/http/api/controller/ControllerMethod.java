package com.ejc.http.api.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;

@Getter
@Value
@RequiredArgsConstructor
public class ControllerMethod {
    private final Class<?> controllerClass;
    private final String methodName;
    private final Class<?> parameterTypes;
    private final Collection<ParameterProvider<?>> parameterProviders;
    private final String httpMethod;
    private final UrlPattern urlPattern;

    public boolean httpMethodMatches(HttpServletRequest request) {
        return request.getMethod().equals(httpMethod);
    }

    public boolean pathMatches(HttpServletRequest request) {
        return urlPattern.matcher(request.getRequestURI()).matches();
    }

    public Map<String, String> getPathVariables(HttpServletRequest request) {
        return urlPattern.matcher(request.getRequestURI()).getPathVariables();
    }


}
