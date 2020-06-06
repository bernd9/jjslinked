package com.ejaf;

import java.lang.reflect.Method;

public interface ParameterProvider {

    <T> T getParameter(Method method, Class<T> targetType);
}
