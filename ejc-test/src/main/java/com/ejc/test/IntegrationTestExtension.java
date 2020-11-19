package com.ejc.test;

import com.ejc.ApplicationRunner;
import com.ejc.util.ClassUtils;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Optional;

public class IntegrationTestExtension implements BeforeTestExecutionCallback {

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        Object test = context.getTestInstance().orElseGet(() -> ClassUtils.createInstance(context.getTestClass().orElseThrow()));
        IntegrationTestInitializer initializer = new IntegrationTestInitializer(test);
        initializer.init();
        ApplicationRunner.run(Optional.of(initializer));
    }
}
