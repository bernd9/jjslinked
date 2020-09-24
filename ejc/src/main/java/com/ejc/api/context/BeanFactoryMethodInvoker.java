package com.ejc.api.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class BeanFactoryMethodInvoker {

    @Getter
    private final ClassReference declaringClass;
    private final String methodName;

    Collection<Object> doInvoke(ApplicationContextFactoryBase factoryBase) {
        return factoryBase.getConfigurations(declaringClass.getReferencedClass()).stream()
                .map(this::doInvokeMethod)
                .collect(Collectors.toSet());
    }

    private Object doInvokeMethod(Object bean) {
        try {
            Method method = bean.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method.invoke(bean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
