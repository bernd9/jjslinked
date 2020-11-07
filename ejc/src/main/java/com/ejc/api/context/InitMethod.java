package com.ejc.api.context;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

@RequiredArgsConstructor
class InitMethod {
    private final String methodName;

    void doInvokeMethod(Object bean) {
        try {
            Method method = bean.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(bean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
