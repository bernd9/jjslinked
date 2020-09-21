package com.ejc.api.context;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.Function;

@RequiredArgsConstructor
class InitInvoker {
    private final ClassReference declaringClass;
    private final String methodName;

    void doInvoke(Function<Class<?>, Set<?>> selectFunction) {
        selectFunction.apply(declaringClass.getReferencedClass()).forEach(bean -> doInvokeMethod(bean));
    }

    private void doInvokeMethod(Object bean) {
        try {
            Method method = bean.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(bean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
