package com.ejc.http.api.controller;

import com.ejc.util.InstanceUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class ControllerMethod {
    private final Class<?> controllerClass;
    private final String methodName;
    private final Collection<String> parameterTypes;
    private final String httpMethod;
    private final UrlPattern urlPattern;
    private List<Class<?>> parameterClasses;

    public boolean httpMethodMatches(HttpServletRequest request) {
        return request.getMethod().equals(httpMethod);
    }

    public boolean pathMatches(HttpServletRequest request) {
        return urlPattern.matcher(request.getRequestURI()).matches();
    }

    public Map<String, String> getPathVariables(HttpServletRequest request) {
        return urlPattern.matcher(request.getRequestURI()).getPathVariables();
    }

    public List<Class<?>> getParameterClasses() {
        if (parameterClasses == null) {
            parameterClasses = parameterTypes.stream().map(InstanceUtils::classForName).collect(Collectors.toList());
        }
        return parameterClasses;
    }

}
