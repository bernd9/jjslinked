package com.ejc.test;

import com.ejc.Inject;
import com.ejc.http.HttpMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(HttpIntegrationTestExtension.class)
class HttpRequestMockTest {

    @Inject
    private HttpRequestMock httpRequestMock;

    @Test
    void test() {
        HttpTestResponse response = httpRequestMock.httpMethod(HttpMethod.GET).path("/").send();
    }
}

