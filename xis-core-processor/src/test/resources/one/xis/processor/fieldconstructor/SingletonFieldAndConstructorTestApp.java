package one.xis.processor.fieldconstructor;

import com.ejc.Application;
import com.ejc.ApplicationRunner;
import com.ejc.Inject;
import com.ejc.Singleton;

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