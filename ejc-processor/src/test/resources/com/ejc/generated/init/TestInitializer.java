package com.ejc.generated.singleton;

import com.ejc.processor.Initializer;
import com.ejc.processor.InitializerBase;

@Initializer
public class TestInitializer extends InitializerBase {
    public TestInitializer() {
        super("com.ejc.generated.singleton.TestBean", "testMethod");
    }
}