package com.ejc.http.api.controller;

interface ParameterProvider<T> {
    T provide(ControllerMethodInvocationContext context);
}
