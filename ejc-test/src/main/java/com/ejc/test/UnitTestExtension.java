package com.ejc.test;

import com.ejc.util.ClassUtils;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class UnitTestExtension implements BeforeTestExecutionCallback {


    @Override
    public void beforeTestExecution(ExtensionContext context) {
        Object test = context.getTestInstance().orElseGet(() -> ClassUtils.createInstance(context.getTestClass().orElseThrow()));
        UnitTestContext unitTestContext = new UnitTestContextBuilder(test).createUnitTestContext();
        new UnitTestFieldInitializer(unitTestContext).setTestFieldValues();
    }
}


