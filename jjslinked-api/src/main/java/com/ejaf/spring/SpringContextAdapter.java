package com.ejaf.spring;

import com.ejaf.Context;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

@RequiredArgsConstructor
public class SpringContextAdapter implements Context {

    private final ApplicationContext applicationContext;

    @Override
    public <T> T getBean(Class<T> type) {
        return applicationContext.getBean(type);
    }
}
