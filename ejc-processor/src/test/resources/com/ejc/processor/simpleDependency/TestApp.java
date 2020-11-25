package com.ejc.processor.simpleDependency;

import com.ejc.Application;
import com.ejc.ApplicationRunner;
import com.ejc.Inject;
import com.ejc.Singleton;

@Application
class TestApp {
    public static void main(String[] args) {
        ApplicationRunner.run(TestApp.class);
    }
}

@Singleton
class Singleton1 {

    @Inject
    private Singleton2 dependency;

}

@Singleton
class Singleton2 {
    private Singleton3 dependency;

    Singleton2(Singleton3 singleton3) {
        dependency = singleton3;
    }

}

@Singleton
class Singleton3 {


}



