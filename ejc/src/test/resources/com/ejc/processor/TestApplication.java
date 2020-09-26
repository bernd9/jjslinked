package com.ejc.processor;

import com.ejc.*;
import lombok.Getter;

import java.util.Set;

@Application
class TestApplication {

    public static void main(String[] args) {
        ApplicationRunner.run(TestApplication.class);
    }
}

@Getter
@Singleton
class TestSingleton1 {
    private final TestSingleton4 singleton4;

    @Value(key = "port", defaultValue = "80")
    private int port;

    @Inject
    private TestSingleton5 singleton5;

    TestSingleton1(TestSingleton4 singleton4) {
        this.singleton4 = singleton4;
    }
}

@Getter
@Singleton
class TestSingleton2 {
    @Inject
    private TestSingleton4 singleton4;
}

@Getter
@Singleton
class TestSingleton3 {
    private final TestSingleton1 singleton1;
    private final TestSingleton2 singleton2;

    @Inject
    private TestSingleton4 singleton4;

    private boolean initInvoked;

    @Init
    void init() {
        initInvoked = true;
    }

    TestSingleton3(TestSingleton1 singleton1, TestSingleton2 singleton2) {
        this.singleton1 = singleton1;
        this.singleton2 = singleton2;
    }
}

@Getter
class TestSingleton4 {

    private final TestSingleton5 singleton5;

    TestSingleton4(TestSingleton5 singleton5) {
        this.singleton5 = singleton5;
    }
}

@Getter
@Singleton
class TestSingleton5 implements TestInterface {

}

@Getter
@Singleton
class TestSingleton6 {
    private final Set<TestInterface> singletons;

    TestSingleton6(Set<TestInterface> singletons) {
        this.singletons = singletons;
    }

}


@Getter
@Configuration
class Config1 {

    @Inject
    private Set<TestInterface> singletons;

    private final TestSingleton5 singleton5;

    private boolean initInvoked;

    Config1(TestSingleton5 singleton5) {
        this.singleton5 = singleton5;
    }

    @Init
    void init() {
        initInvoked = true;
    }

    @Bean
    TestSingleton4 singleton4() {
        return new TestSingleton4(singleton5);
    }
}

interface TestInterface {

}