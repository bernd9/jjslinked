package com.ejc.test.app;

import com.ejc.Inject;
import com.ejc.http.HttpMethod;
import com.ejc.http.test.HttpIntegrationTestExtension;
import com.ejc.http.test.HttpMock;
import com.ejc.http.test.HttpTestResponse;
import com.ejc.test.Mock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(HttpIntegrationTestExtension.class)
public class GreetingTest {

    @Inject
    private HttpMock httpMock;

    @Mock
    private GreetingService greetingService;

    @BeforeAll
    void init() {
        System.out.println("BeforeEach");
        // when(greetingService.getGreeting()).thenReturn(new Greeting("Huhu ! Ich bin's."));
    }

    @Test
    void getGreeting() {
        HttpTestResponse response = httpMock
                .httpMethod(HttpMethod.GET)
                .send();
    }
}
