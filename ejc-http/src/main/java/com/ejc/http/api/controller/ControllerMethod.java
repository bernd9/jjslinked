package com.ejc.http.api.controller;

import com.ejc.http.HttpMethod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
public class ControllerMethod {
    private final Class<?> controllerClass;
    private final String methodName;
    private final List<Class<?>> parameterTypes = new ArrayList<>();
    private final List<ParameterProvider<?>> parameterProviders = new ArrayList<>();
    private final HttpMethod httpMethod;
    private final UrlPattern urlPattern;

    public boolean httpMethodMatches(HttpServletRequest request) {
        return request.getMethod().equals(httpMethod.name());
    }

    public boolean pathMatches(HttpServletRequest request) {
        return urlPattern == null || urlPattern.matcher(request.getRequestURI()).matches();
    }

    public Map<String, String> getPathVariables(HttpServletRequest request) {
        return urlPattern.matcher(request.getRequestURI()).getPathVariables();
    }

    @SuppressWarnings("unused")
    public void addParameterType(Class<?> c) {
        parameterTypes.add(c);
    }

    @SuppressWarnings("unused")
    public void addParameterProvider(ParameterProvider<?> provider) {
        parameterProviders.add(provider);
    }

}