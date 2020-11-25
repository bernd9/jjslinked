package com.ejc.processor;

import com.ejc.Application;
import com.ejc.ApplicationRunner;
import com.ejc.Inject;
import com.ejc.Singleton;

@Application
class InheritedDependencyInjectionTestApp {

    public static void main(String[] args) {
        ApplicationRunner.run(InheritedDependencyInjectionTestApp.class);
    }
}


class SingletonBase {

    @Inject
    protected Interface field;

}

@Singleton
class Singleton3 extends SingletonBase {


}

@Singleton
class Singleton4 implements Interface {

}


interface Interface {

}
