package com.ejc.test;

import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class UnitTestExtension implements BeforeTestExecutionCallback {


    @Override
    public void beforeTestExecution(ExtensionContext context) {
        Object test = JUnit5Util.getTestInstance(context);
        if (test.getClass().isAnnotationPresent(ActivateProfile.class)) {
            throw new IllegalStateException("@ActivateProfile is only valid in integration tests");
        }
        new UnitTestFieldInitializer(test).setTestFieldValues();
    }
}


