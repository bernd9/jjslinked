package com.ejc.api.context;

import com.ejc.ApplicationRunner;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
public class ApplicationRunnerBase extends ApplicationRunner {

    private final Class<?> applicationClass;

    @Override
    public void doRun(Collection<SingletonProcessor> singletonProcessors) {
        ApplicationContextFactory factory = new ApplicationContextFactory(applicationClass);
        singletonProcessors.forEach(factory::addSingletonProcessor);
        factory.createApplicationContext();
    }
}
