package com.ejc.test;

import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

class HttpTestClient {

    HttpTestResponse send(HttpRequestMock requestMock) {

        return null;
    }


    private HttpServletRequest mockedRequest(HttpRequestMock requestMock) {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        return request;
    }

}
