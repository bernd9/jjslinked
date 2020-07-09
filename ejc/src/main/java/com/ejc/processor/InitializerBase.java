package com.ejc.processor;

import com.ejc.ApplicationContext;
import com.ejc.Init;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class InitializerBase {
    private final String beanName;
    private final String methodName;

    public void invokeInit(ApplicationContext context) {
        try {
            doInvokeInTypeHierarchy(context.getBeans((Class<Object>) BeanUtils.classForName(beanName)), methodName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void doInvokeInTypeHierarchy(@NonNull Set<Object> beans, String method) {
        List<Object> beansSorted = new ArrayList<>(beans);
        Collections.sort(beansSorted, this::compare);
        for (Object bean : beansSorted) {
            if (doInvoke(bean, method)) {
                break;
            }
        }
    }

    private boolean doInvoke(Object bean, String methodName) {
        try {
            Method method = bean.getClass().getDeclaredMethod(methodName);
            if (method.isAnnotationPresent(Init.class)) {
                method.setAccessible(true);
                method.invoke(bean);
                return true;
            }
            return false;
        } catch (NoSuchMethodException nsm) {
            // May happen when file is out of sync
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int compare(Object c1, Object c2) {
        return getInheritanceLevel(c2.getClass()).compareTo(getInheritanceLevel(c1.getClass()));
    }

    private static Integer getInheritanceLevel(Class<?> c) {
        int result = 0;
        while (c != null && !c.equals(Object.class)) {
            result++;
            c = c.getSuperclass();
        }
        return result;
    }

}
