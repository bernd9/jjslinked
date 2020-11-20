package com.ejc.test;

import com.ejc.test.testapp.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.util.MockUtil;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(UnitTestExtension.class)
@TestInstance(org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS)
public class UnittestTest {

    @TestSubject
    @InjectConfigValue(name = "host", value = "localhost")
    @InjectConfigValue(name = "port", value = "8080")
    private Singleton2 singleton2;

    @TestSubject
    @InjectConfigValue(name = "host", value = "localhost")
    @InjectConfigValue(name = "port", value = "8080")
    private Singleton3 singleton3;

    @TestDependency
    private Singleton1 singleton1;

    @Mock
    private Singleton4 singleton4;

    @Spy
    private Singleton5 singleton5 = new Singleton5();

    @BeforeAll
    void beforeEach() {
        System.out.println("beforeEach");
        singleton1 = new Singleton1();
    }

    // TODO Collection field and constructor-parameter-test
    @Test
    void configFieldInjection() {
        assertThat(singleton2.getPort()).isEqualTo(8080);
        assertThat(singleton2.getHost()).isEqualTo("localhost");
    }

    @Test
    void testDependencyFieldInjection() {
        assertThat(singleton2.getSingleton1()).isNotNull();
    }

    @Test
    void mockFieldInjection() {
        assertThat(singleton2.getSingleton4()).isNotNull();
        assertThat(MockUtil.isMock(singleton2.getSingleton4())).isTrue();
    }

    @Test
    void spyFieldInjection() {
        assertThat(singleton2.getSingleton5()).isNotNull();
        assertThat(MockUtil.isSpy(singleton2.getSingleton5())).isTrue();
    }

    @Test
    void mockFieldIfUndefined() {
        assertThat(singleton2.getSingleton6()).isNotNull();
        assertThat(MockUtil.isMock(singleton2.getSingleton6())).isTrue();
    }

    @Test
    void constructorInjection() {
        assertThat(singleton3.getPort()).isEqualTo(8080);
        assertThat(singleton3.getHost()).isEqualTo("localhost");
        assertThat(singleton3.getSingleton1()).isNotNull();
        assertThat(singleton3.getSingleton4()).isNotNull();
        assertThat(singleton3.getSingleton5()).isNotNull();
        assertThat(singleton3.getSingleton6()).isNotNull();

        assertThat(MockUtil.isMock(singleton3.getSingleton4())).isTrue();
        assertThat(MockUtil.isSpy(singleton3.getSingleton5())).isTrue();
        assertThat(MockUtil.isMock(singleton3.getSingleton6())).isTrue();
    }

}
