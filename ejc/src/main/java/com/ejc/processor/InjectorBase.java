package com.ejc.processor;

import com.ejc.ApplicationContext;
import com.ejc.Inject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InjectorBase {
    private final String declaringClass;
    private final String fieldName;
    private final String fieldType;

    public void doInject(ApplicationContext context) {
        try {
            BeanUtils.doInjectInTypeHirarachy(context.getBeans((Class<Object>) BeanUtils.classForName(declaringClass)), fieldName, context.getBean(fieldType), Inject.class);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
