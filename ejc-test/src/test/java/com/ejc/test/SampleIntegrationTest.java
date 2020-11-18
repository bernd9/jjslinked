package com.ejc.test;

import com.ejc.Application;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(IntegrationTestExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SampleIntegrationTest {


    @Application
    class TestApp {

    }
}
