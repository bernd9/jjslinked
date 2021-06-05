package one.xis.processor.beanMethod;

import com.ejc.*;

@Application
class TestApp {
    
}


@Configuration
class Config1 {

    @Inject
    private Interface1 dependency2;
    private final Singleton1 dependency1;

    Config1(Singleton1 dependency1) {
        this.dependency1 = dependency1;
    }

    @Bean
    Bean1 bean(Singleton3 singleton3) {
        return new Bean1(dependency1, dependency2, singleton3);
    }
}


@Singleton
class Singleton1 {

}

@Singleton
class Singleton2 implements Interface1 {

}

class Bean1 {
    private final Singleton1 dependency1;
    private final Interface1 dependency2;
    private final Singleton3 dependency3;


    Bean1(Singleton1 dependency1, Interface1 dependency2, Singleton3 dependency3) {
        this.dependency1 = dependency1;
        this.dependency2 = dependency2;
        this.dependency3 = dependency3;
    }
}

@Singleton
class Singleton3 {

}

interface Interface1 {

}