package com.ejaf;

import java.lang.reflect.Method;
import java.util.List;

public interface MethodInvocationListener {

    void onMethodInvocation(Method method, List<MethodParameter> parameters) throws Throwable;
}
