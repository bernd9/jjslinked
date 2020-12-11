package com.ejc.http.test;

import com.ejc.api.context.ApplicationContext;
import com.ejc.http.api.controller.ControllerMethodInvoker;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import static org.mockito.Mockito.when;

class HttpTestClient {

    HttpTestResponse send(HttpMock httpMock) {
        ControllerMethodInvoker invoker = ApplicationContext.getInstance().getBean(ControllerMethodInvoker.class);
        HttpTestResponse httpTestResponse = new HttpTestResponse();
        invoker.invoke(mockedRequest(httpMock), mockedResponse(httpMock, httpTestResponse));
        return httpTestResponse;
    }


    private HttpServletRequest mockedRequest(HttpMock httpMock) {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn(httpMock.getHttpMethod().name());
        when(request.getRequestURI()).thenReturn(httpMock.getPath());
        // TODO
        return request;
    }

    private HttpServletResponse mockedResponse(HttpMock httpMock, HttpTestResponse httpTestResponse) {
        return new HttpResponse(Mockito.mock(HttpServletResponse.class), httpTestResponse);
    }

    class HttpResponse extends HttpServletResponseWrapper {
        private final HttpTestResponse httpTestResponse;

        public HttpResponse(HttpServletResponse response, HttpTestResponse httpTestResponse) {
            super(response);
            this.httpTestResponse = httpTestResponse;
        }
        // TODO
    }

}
