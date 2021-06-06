package one.xis.processor.simpleDependency;

import one.xis.Application;
import one.xis.Inject;
import one.xis.Singleton;

@Application
class TestApp {
 
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



