package com.ejc.processor.singletonconstr;

import com.ejc.Application;
import com.ejc.ApplicationRunner;
import com.ejc.Singleton;

@Application
class SingletonConstructorTestApp {

    public static void main(String[] args) {
        ApplicationRunner.run(SingletonConstructorTestApp.class);
    }
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