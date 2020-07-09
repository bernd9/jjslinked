package com.ejc.processor;

import com.ejc.ApplicationContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InjectorBase {
    private final String declaringClass;
    private final String fieldName;
    private final String fieldType;

    public void doInject(ApplicationContext context) {
        try {
            BeanUtils.doInjectInTypeHirarachy(context.getBeans((Class<Object>) BeanUtils.classForName(declaringClass)), fieldName, fieldType);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
