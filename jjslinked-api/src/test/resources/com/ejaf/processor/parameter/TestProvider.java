package com.ejaf.processor;

import com.ejaf.ParameterContext;
import com.ejaf.ParameterProvider;

public class TestProvider implements ParameterProvider<TestEvent> {

    public <T> T getParameter(ParameterContext parameterContext, TestEvent event, Class<T> type) {
        return null;
    }

    public boolean isSupportedParameterType(Class<?> c) {
        return true;
    }
}
