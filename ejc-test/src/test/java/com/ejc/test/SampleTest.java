package com.ejc.test;

import com.ejc.Inject;
import com.ejc.Singleton;
import com.ejc.Value;
import com.ejc.test.*;
import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.util.MockUtil;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(UnitTestExtension.class)
@TestInstance(org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS)
public class SampleTest {

    @TestSubject
    @InjectConfigValue(name = "host", value = "localhost")
    @InjectConfigValue(name = "port", value = "8080")
    @InjectTestDependencies
    private Singleton2 singleton2;

    @TestSubject
    @InjectConfigValue(name = "host", value = "localhost")
    @InjectConfigValue(name = "port", value = "8080")
    @InjectTestDependencies
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

    @Test
    void fieldInjection() {
        assertThat(singleton2.getPort()).isEqualTo(8080);
        assertThat(singleton2.getHost()).isEqualTo("localhost");
        assertThat(singleton2.getSingleton1()).isNotNull();
        assertThat(singleton2.getSingleton4()).isNotNull();
        assertThat(singleton2.getSingleton5()).isNotNull();
        assertThat(singleton2.getSingleton6()).isNotNull();

        assertThat(MockUtil.isMock(singleton2.getSingleton4())).isTrue();
        assertThat(MockUtil.isSpy(singleton2.getSingleton5())).isTrue();
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

    @Singleton
    class Singleton1 {

    }

    @Getter
    @Singleton
    class Singleton2 {

        @Value("port")
        private int port;

        @Value("host")
        private String host;

        @Inject
        private Singleton1 singleton1;

        @Inject
        private Singleton4 singleton4;

        @Inject
        private Singleton5 singleton5;

        @Inject
        private Singleton6 singleton6;
    }

    @Getter
    @Singleton
    class Singleton3 {

        private String host;
        private int port;
        private Singleton1 singleton1;
        private Singleton4 singleton4;
        private Singleton5 singleton5;
        private Singleton6 singleton6;

        Singleton3(Singleton1 singleton1,
                   @Value("host") String host,
                   @Value("port") int port,
                   Singleton4 singleton4,
                   Singleton5 singleton5,
                   Singleton6 singleton6) {
            this.singleton1 = singleton1;
            this.singleton4 = singleton4;
            this.singleton5 = singleton5;
            this.singleton6 = singleton6;
            this.host = host;
            this.port = port;
        }
    }

    @Singleton
    class Singleton4 {

    }


    @Singleton
    class Singleton5 {

    }


    @Singleton
    class Singleton6 {

    }
}
