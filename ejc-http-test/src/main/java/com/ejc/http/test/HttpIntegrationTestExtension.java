package com.ejc.http.test;

import com.ejc.ApplicationRunner;
import com.ejc.http.api.JettyBoostrapDisabledPreProcessor;
import com.ejc.test.IntegrationTestInitializer;
import com.ejc.test.JUnit5Util;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class HttpIntegrationTestExtension implements BeforeTestExecutionCallback {
    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        IntegrationTestInitializer initializer = new IntegrationTestInitializer(JUnit5Util.getTestInstance(context));
        ApplicationRunner.addSingletonPreSupplier(HttpMock::new);
        ApplicationRunner.addSingletonPreProcessor(initializer);
        ApplicationRunner.addSingletonPreProcessor(new JettyBoostrapDisabledPreProcessor());
        ApplicationRunner.run();
    }
}
