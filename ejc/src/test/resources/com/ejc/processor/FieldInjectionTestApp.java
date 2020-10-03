package com.ejc.processor;

import com.ejc.Application;
import com.ejc.ApplicationRunner;
import com.ejc.Inject;
import com.ejc.Singleton;

@Application
class FieldInjectionTestApp {

    public static void main(String[] args) {
        ApplicationRunner.run(FieldInjectionTestApp.class);
    }
}


@Singleton
class Singleton1 {
    @Inject
    private Singleton2 singleton2;

}

@Singleton
class Singleton2 {

}
