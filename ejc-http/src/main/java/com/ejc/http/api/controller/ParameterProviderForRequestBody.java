package com.ejc.http.api.controller;

import com.ejc.api.context.ClassReference;
import com.ejc.http.HttpStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@Getter
@RequiredArgsConstructor
public class ParameterProviderForRequestBody implements ParameterProvider {
    private final ClassReference parameterType;

    @Override
    public Object provide(ControllerMethodInvocationContext context) {
        ObjectMapper objectMapper = context.getApplicationContext().getBean(ObjectMapper.class);
        try {
            return objectMapper.readValue(context.getRequest().getInputStream(), parameterType.getClazz());
        } catch (IOException e) {
            throw new HttpStatusException(422, "unable to read body content");
        }
    }
}