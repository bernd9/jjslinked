package com.ejc.api.context;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class TestModuleFactory extends ModuleFactory {
    public TestModuleFactory() {
        super(ClassReference.getRef("com.ejc.api.context.TestModuleFactory"));
    }
}
