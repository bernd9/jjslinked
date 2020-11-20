package com.ejc.test;

import com.ejc.Inject;
import com.ejc.test.testapp.Singleton1;
import com.ejc.test.testapp.Singleton2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(IntegrationTestExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SampleIntegrationTest {

    @Mock
    private Singleton1 singleton1;


    @Inject
    private Singleton2 singleton2;


    @Test
    void test() {
        assertThat(singleton2.getSingleton1()).isNotNull();
    }
}
