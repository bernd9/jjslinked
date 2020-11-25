package com.ejc.processor.collectionDependency;

import com.ejc.*;

import java.util.Set;

@Application
class TestApp {
    public static void main(String[] args) {
        ApplicationRunner.run(TestApp.class);
    }
}

@Configuration
class Configuration1 {

    @Inject
    private Set<Interface1> dependency;

}

@Configuration
class Configuration2 {

    private Set<Interface1> dependency;

    Configuration2(Set<Interface1> dependency) {
        this.dependency = dependency;
    }

}

interface Interface1 {


}

@Singleton
class Singleton2 implements Interface1 {

}

@Singleton
class Singleton3 extends Singleton2 {


}




