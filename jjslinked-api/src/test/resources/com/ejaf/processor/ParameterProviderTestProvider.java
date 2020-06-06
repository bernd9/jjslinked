package com.ejaf.processor;


import com.ejaf.ParameterProvider;

import java.lang.reflect.Method;


public class ParameterProviderTestProvider implements ParameterProvider<String> {

    public String getParameter(String parameterName, Method method) {
        return null;
    }
}
