package com.ejc.test.app;

import com.ejc.Inject;
import com.ejc.http.HttpMethod;
import com.ejc.http.test.HttpIntegrationTestExtension;
import com.ejc.http.test.HttpMock;
import com.ejc.http.test.HttpTestResponse;
import com.ejc.test.Mock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(HttpIntegrationTestExtension.class)
public class GreetingTest {

    @Inject
    private HttpMock httpMock;

    @Mock
    private GreetingService greetingService;

    @BeforeEach
    void init() {
        // when(greetingService.getGreeting()).thenReturn(new Greeting("Huhu ! Ich bin's."));
    }

    @Test
    void getGreeting() {
        HttpTestResponse response = httpMock
                .httpMethod(HttpMethod.GET)
                .send();
    }
}
