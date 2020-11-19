package com.ejc.test;

import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class UnitTestExtension implements BeforeTestExecutionCallback {


    @Override
    public void beforeTestExecution(ExtensionContext context) {
        new UnitTestFieldInitializer(JUnit5Util.getTestInstance(context)).setTestFieldValues();
    }
}


