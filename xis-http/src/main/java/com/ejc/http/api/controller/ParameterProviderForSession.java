package com.ejc.http.api.controller;

import javax.servlet.http.HttpSession;

public class ParameterProviderForSession implements ParameterProvider<HttpSession> {
    @Override
    public HttpSession provide(ControllerMethodInvocationContext context) {
        return context.getRequest().getSession();
    }
}
