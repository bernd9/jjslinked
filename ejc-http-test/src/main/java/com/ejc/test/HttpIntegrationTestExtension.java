package com.ejc.test;

import com.ejc.ApplicationRunner;
import com.ejc.http.api.JettyBoostrapDisabledPreProcessor;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class HttpIntegrationTestExtension implements BeforeTestExecutionCallback {
    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        IntegrationTestInitializer initializer = new IntegrationTestInitializer(JUnit5Util.getTestInstance(context));
        initializer.setTestFieldValue(new HttpMock());
        ApplicationRunner.addSingletonPreProcessor(initializer);
        ApplicationRunner.addSingletonPreProcessor(new JettyBoostrapDisabledPreProcessor());
        ApplicationRunner.run();
    }
}
