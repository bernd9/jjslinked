package com.ejaf.processor;

import com.ejaf.ParameterContext;
import com.ejaf.ParameterProvider;

public class TestProvider implements ParameterProvider<TestAnnotation, TestInvocationContext> {

    public <T> T getParameter(ParameterContext<TestAnnotation, TestInvocationContext> parameterContext, Class<T> type) {
        return null;
    }

    public boolean isSupportedParameterType(Class<?> c) {
        return true;
    }
}
