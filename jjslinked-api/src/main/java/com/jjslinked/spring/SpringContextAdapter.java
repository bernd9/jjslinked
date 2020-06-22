package com.jjslinked.spring;

import com.jjslinked.ApplicationContext;
import com.jjslinked.parameter.ParameterProviderRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

@RequiredArgsConstructor
public class SpringContextAdapter implements ApplicationContext {

    private final org.springframework.context.ApplicationContext applicationContext;
    private final ParameterProviderRegistry parameterProviderRegistry = new ParameterProviderRegistry();


    @Override
    public <T> T getBean(Class<T> type) {
        return getCustomerBean(type);
    }


    private <T> T getCustomerBean(Class<T> type) {
        try {
            return applicationContext.getBean(type);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }
}
