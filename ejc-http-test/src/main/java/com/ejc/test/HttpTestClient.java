package com.ejc.test;

import com.ejc.api.context.ApplicationContext;
import com.ejc.http.api.controller.ControllerMethodInvoker;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class HttpTestClient {

    HttpTestResponse send(HttpMock httpMock) {
        ControllerMethodInvoker invoker = ApplicationContext.getInstance().getBean(ControllerMethodInvoker.class);
        HttpTestResponse response = new HttpTestResponse();
        invoker.invoke(mockedRequest(httpMock), mockedResponse(httpMock, response));
        return response;
    }


    private HttpServletRequest mockedRequest(HttpMock httpMock) {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        // TODO
        return request;
    }

    private HttpServletResponse mockedResponse(HttpMock httpMock, HttpTestResponse httpTestResponse) {
        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
        // TODO
        return httpServletResponse;
    }

}
