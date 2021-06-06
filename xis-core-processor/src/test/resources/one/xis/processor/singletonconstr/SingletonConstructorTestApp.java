package one.xis.processor.singletonconstr;

import one.xis.Application;
import one.xis.Singleton;

@Application
class SingletonConstructorTestApp {

    
}

@Singleton
class Singleton1 {
    private final Singleton2 dependency1;
    private final Interface1 dependency2;

    Singleton1(Singleton2 dependency1, Interface1 dependency2) {
        this.dependency1 = dependency1;
        this.dependency2 = dependency2;
    }
}


@Singleton
class Singleton2 {

}

@Singleton
class Singleton3 implements Interface1 {

}

interface Interface1 {

}