package com.ejc.test;

import com.ejc.Application;
import com.ejc.Inject;
import com.ejc.Singleton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(IntegrationTestExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SampleIntegrationTest {

    @TestSubject
    private Service1 service1;

    @Mock
    private Dao1 dao1;

    @Test
    void test() {

    }

    @Application
    class TestApp {

    }

    @Singleton
    class Service1 {

        @Inject
        private Dao1 dao;
    }

    @Singleton
    class Dao1 {

    }
}
