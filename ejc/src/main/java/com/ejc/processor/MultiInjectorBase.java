package com.ejc.processor;

import com.ejc.ApplicationContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MultiInjectorBase {
    private final String declaringClass;
    private final String fieldName;
    private final String genericType;

    public void doInject(ApplicationContext context) {
        try {
            BeanUtils.doInjectInTypeHirarachy(context.getBeans((Class<Object>) BeanUtils.classForName(declaringClass)), fieldName, context.getBeans(BeanUtils.classForName(genericType)));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
