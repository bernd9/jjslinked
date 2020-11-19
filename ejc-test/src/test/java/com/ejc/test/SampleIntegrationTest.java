package com.ejc.test;

import com.ejc.Inject;
import com.ejc.test.testapp.Singleton1;
import com.ejc.test.testapp.Singleton2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(IntegrationTestExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SampleIntegrationTest {

    @Inject
    private Singleton1 singleton1;

    @Mock
    private Singleton2 singleton2;


    @Test
    void test() {
        System.out.println(singleton1.getSingleton2());
    }
}
