package one.xis.processor.fieldconstructor;

import one.xis.Application;
import one.xis.ApplicationRunner;
import one.xis.Inject;
import one.xis.Singleton;

@Application
class SingletonFieldAndConstructorTestApp {

    public static void main(String[] args) {
        ApplicationRunner.run(SingletonFieldAndConstructorTestApp.class);
    }
}

@Singleton
class Singleton1 {
    @Inject
    private Interface1 dependency2;
    private final Singleton2 dependency1;

    Singleton1(Singleton2 dependency1) {
        this.dependency1 = dependency1;
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