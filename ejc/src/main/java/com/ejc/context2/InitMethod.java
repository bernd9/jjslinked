package com.ejc.context2;

import com.ejc.api.context.ClassReference;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

@RequiredArgsConstructor
class InitMethod {
    private final ClassReference declaringClass;
    private final String name;

    void invoke(Object o) {
        try {
            getMethod(o).invoke(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Method getMethod(Object o) throws NoSuchMethodException {
        Class<?> c = o.getClass();
        while (!c.equals(Object.class)) {
            if (c.equals(declaringClass.getReferencedClass())) {
                try {
                    return c.getDeclaredMethod(name);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
            c = c.getSuperclass();
        }
        throw new NoSuchMethodException();
    }

}
