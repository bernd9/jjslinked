package com.ejc.http.api.controller;

import one.xis.context.ApplicationContext;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Getter
@Builder
@RequiredArgsConstructor
public class ControllerMethodInvocationContext {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final Map<String, String> pathVariables;
    private final ApplicationContext applicationContext;
}
