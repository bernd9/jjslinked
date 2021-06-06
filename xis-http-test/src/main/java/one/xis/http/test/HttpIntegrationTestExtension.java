package one.xis.http.test;

import one.xis.ApplicationRunner;
import one.xis.http.api.JettyBoostrapDisabledPreProcessor;
import one.xis.test.IntegrationTestInitializer;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

public class HttpIntegrationTestExtension implements TestInstancePostProcessor {
    
    @Override
    public void postProcessTestInstance(Object o, ExtensionContext context) throws Exception {
        IntegrationTestInitializer initializer = new IntegrationTestInitializer(o);
        ApplicationRunner.addSingletonPreSupplier(HttpMock::new);
        ApplicationRunner.addSingletonPreProcessor(initializer);
        ApplicationRunner.addSingletonPreProcessor(new JettyBoostrapDisabledPreProcessor());
        ApplicationRunner.run();
    }
}
