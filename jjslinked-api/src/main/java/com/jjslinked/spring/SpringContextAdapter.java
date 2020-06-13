package com.jjslinked.spring;

import com.jjslinked.ApplicationContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpringContextAdapter implements ApplicationContext {

    private final org.springframework.context.ApplicationContext applicationContext;

    @Override
    public <T> T getBean(Class<T> type) {
        return applicationContext.getBean(type);
    }
}