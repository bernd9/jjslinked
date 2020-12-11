package com.ejc.test;

import com.ejc.Inject;
import com.ejc.http.HttpMethod;
import com.ejc.http.test.HttpIntegrationTestExtension;
import com.ejc.http.test.HttpMock;
import com.ejc.http.test.HttpTestResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(HttpIntegrationTestExtension.class)
class HttpMockTest {

    @Inject
    private HttpMock httpMock;

    @Test
    void test() {
        HttpTestResponse response = httpMock.httpMethod(HttpMethod.GET).path("/").send();
    }
}

