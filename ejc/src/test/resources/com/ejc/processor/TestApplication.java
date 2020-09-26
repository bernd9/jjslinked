package com.ejc.processor;

import com.ejc.*;
import lombok.Getter;

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
class TestSingleton5 {

}

@Getter
@Configuration
class Config1 {

    @Inject
    private TestSingleton5 singleton5;

    @Bean
    TestSingleton4 singleton4() {
        return new TestSingleton4(singleton5);
    }
}