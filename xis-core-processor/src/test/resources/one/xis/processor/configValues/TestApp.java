package one.xis.processor.configValues;

import one.xis.Application;
import one.xis.Singleton;
import one.xis.Value;

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
