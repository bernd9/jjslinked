package com.ejc.test;

import com.ejc.ApplicationRunner;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

public class IntegrationTestExtension implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        IntegrationTestInitializer initializer = new IntegrationTestInitializer(JUnit5Util.getTestInstance(context));
        ApplicationRunner.addSingletonPreProcessor(initializer);
        ApplicationRunner.run();
    }
}
