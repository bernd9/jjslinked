package com.ejc.test;

import com.ejc.ApplicationRunner;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class IntegrationTestExtension implements BeforeTestExecutionCallback {

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        IntegrationTestInitializer initializer = new IntegrationTestInitializer(JUnit5Util.getTestInstance(context));
        initializer.init();
        ApplicationRunner.addSingletonProcessor(initializer);
        ApplicationRunner.run();
    }
}
