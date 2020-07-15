package com.ejc.generated.singleton;

import com.ejc.processor.SingletonLoader;
import com.ejc.processor.SingletonLoaderBase;

@SingletonLoader("com.ejc.generated.singleton.TestBean")
public class TestSingletonLoader1 extends SingletonLoaderBase {
    public TestSingletonLoader1() {
        super("com.ejc.generated.singleton.TestBean");
    }
}