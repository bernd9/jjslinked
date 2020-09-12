package com.ejc.http.api.controller;

import com.ejc.http.HttpStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class ParameterProviderForRequestBody<T> implements ParameterProvider<T> {
    private final Class<T> parameterType;

    @Override
    public T provide(ControllerMethodInvocationContext context) {
        ObjectMapper objectMapper = context.getApplicationContext().getBean(ObjectMapper.class);
        try {
            return objectMapper.readValue(context.getRequest().getInputStream(), parameterType);
        } catch (IOException e) {
            throw new HttpStatusException(422, "unable to read body content");
        }
    }
}
