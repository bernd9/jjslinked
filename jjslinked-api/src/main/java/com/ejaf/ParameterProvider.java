package com.ejaf;

import java.lang.reflect.Method;

public interface ParameterProvider<T, C extends InvocationContext> {

    T getParameter(String parameterName, Method method, C context);
}
