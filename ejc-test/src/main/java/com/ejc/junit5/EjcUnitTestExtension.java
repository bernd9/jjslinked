package com.ejc.junit5;

import com.ejc.util.ClassUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstanceFactory;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstantiationException;

public class EjcUnitTestExtension implements TestInstanceFactory {
    @Override
    public Object createTestInstance(TestInstanceFactoryContext testInstanceFactoryContext, ExtensionContext extensionContext) throws TestInstantiationException {
        return ClassUtils.createInstance(testInstanceFactoryContext.getTestClass());
   }
}
