package com.ejaf.processor;


import com.ejaf.ParameterProvider;


public class TestProvider implements ParameterProvider<TestAnnotation, TestInvocationContext> {

    public <T> T getParameter(String parameterName, TestAnnotation annotation, TestInvocationContext context, Class<T> type) {
        return null;
    }
}
