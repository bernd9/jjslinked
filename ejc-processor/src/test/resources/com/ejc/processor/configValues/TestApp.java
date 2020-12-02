package com.ejc.processor.configValues;

import com.ejc.Application;
import com.ejc.Singleton;
import com.ejc.Value;

@Application
class TestApp {
    
}

@Singleton
class Singleton1 {

    @Value(value = "integer")
    private int configValue;
}

@Singleton
class Singleton2 {
    private int configValue;
    private Singleton1 dependency;

    Singleton2(Singleton1 dependency, @Value(value = "integer") int configValue) {
        this.dependency = dependency;
        this.configValue = configValue;
    }
}
