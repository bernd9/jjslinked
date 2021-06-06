package one.xis.processor;

import one.xis.Application;
import one.xis.ApplicationRunner;
import one.xis.Inject;
import one.xis.Singleton;

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
