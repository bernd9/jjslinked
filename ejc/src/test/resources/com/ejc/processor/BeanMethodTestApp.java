package com.ejc.processor;

import com.ejc.*;

@Application
class BeanMethodTestApp {

    public static void main(String[] args) {
        ApplicationRunner.run(BeanMethodTestApp.class);
    }
}


@Configuration
class Config1 {

    @Inject
    private Interface1 dependency2;
    private final Singleton2 dependency1;

    Config1(Singleton2 dependency1) {
        this.dependency1 = dependency1;
    }

    @Bean
    Bean1 bean(Singleton4 singleton4) {
        return new Bean1(dependency1, dependency2, singleton4);
    }
}


@Singleton
class Singleton2 {

}

@Singleton
class Singleton3 implements Interface1 {

}

class Bean1 {
    private final Singleton2 dependency1;
    private final Interface1 dependency2;
    private final Singleton4 dependency3;


    Bean1(Singleton2 dependency1, Interface1 dependency2, Singleton4 dependency3) {
        this.dependency1 = dependency1;
        this.dependency2 = dependency2;
        this.dependency3 = dependency3;
    }
}

@Singleton
class Singleton4 {

}

interface Interface1 {

}