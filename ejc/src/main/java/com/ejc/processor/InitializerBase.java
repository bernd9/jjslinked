package com.ejc.processor;

import com.ejc.ApplicationContext;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.ejc.processor.InstanceUtils.classForName;

@RequiredArgsConstructor
public class InitializerBase {
    private final String beanName;
    private final String methodName;

    public void invokeInit(ApplicationContext context) {
        try {
            Class<?> c = classForName(beanName);
            context.getBeans(c).forEach(bean -> doInvoke(bean, c));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void doInvoke(Object bean, Class<?> declaringClass) {
        Class c = bean.getClass();
        while (c != null && !c.equals(Object.class)) {
            if (c.equals(declaringClass)) {
                try {
                    doInvoke(bean, c.getDeclaredMethod(methodName));
                    break;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            c = c.getSuperclass();
        }
    }

    private void doInvoke(Object bean, Method method) throws InvocationTargetException, IllegalAccessException {
        method.setAccessible(true);
        method.invoke(bean);
    }

}
