package com.ejaf;

import java.lang.reflect.Method;

public interface ParameterProvider<T> {

    T getParameter(String parameterName, Method method);
}
