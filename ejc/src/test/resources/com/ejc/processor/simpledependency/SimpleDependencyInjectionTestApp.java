package com.ejc.processor.simpledependency;

import com.ejc.Application;
import com.ejc.ApplicationRunner;
import com.ejc.Inject;
import com.ejc.Singleton;

@Application
class SimpleDependencyInjectionTestApp {

    public static void main(String[] args) {
        ApplicationRunner.run(SimpleDependencyInjectionTestApp.class);
    }
}


@Singleton
class SimpleDependencySingleton1 {
    @Inject
    private SimpleDependencySingleton2 singleton2;

}

@Singleton
class SimpleDependencySingleton2 {

}
