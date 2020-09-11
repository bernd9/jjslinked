package com.ejc.http.api.controller;

import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RequiredArgsConstructor
public class ControllerMethodInvocationContext {
    private final ControllerMethod controllerMethod;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final Map<String, String> pathVariables;
}
