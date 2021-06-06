package one.xis.processor.dependencyAll;

import one.xis.*;

@Application
class TestApp {
    public static void main(String[] args) {
        ApplicationRunner.run(TestApp.class);
    }
}

@Configuration
class Configuration1 {

    @Inject
    private Set<Interface1> dependency;

    @Init
    void init() {
        
    }

    @Bean
    Singelton4 singelton4() {

    }


}

@Singleton
class Interface1 {


}

@Singleton
class Singleton2 implements Interface1 {

}

@Singleton
class Singleton3 extends Singleton2 {


}

class Singelton4 extends Singleton2 {

}

class Singleton5 {

    private List<Interface1> dependency;
}




