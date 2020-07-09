package com.ejc.generated.singleton;

import com.ejc.processor.SingletonLoader;
import com.ejc.processor.SingletonLoaderBase;

@SingletonLoader
public class TestSingletonLoader extends SingletonLoaderBase {
    public TestSingletonLoader() {
        super("com.ejc.generated.singleton.TestBean");
    }
}