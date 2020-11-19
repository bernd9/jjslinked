package com.ejc.test;

import com.ejc.api.context.ApplicationContextFactory;
import com.ejc.api.context.SingletonProcessor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class IntegrationTestFieldInitializer implements SingletonProcessor {
    private final Class<?> applicationClass;
    private final Object test;


    void setTestFieldValues() {
        ApplicationContextFactory contextFactory = new ApplicationContextFactory(applicationClass);
        // add processor this
        
    }


}
