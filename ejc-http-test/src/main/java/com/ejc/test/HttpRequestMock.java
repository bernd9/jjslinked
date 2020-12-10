package com.ejc.test;

import com.ejc.api.context.ApplicationContext;
import com.ejc.http.HttpMethod;
import com.ejc.http.api.controller.ControllerMethodInvoker;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Getter
public class HttpRequestMock {
    private HttpMethod httpMethod = HttpMethod.GET;
    private String path = "";
    private final Map<String, Set<Object>> parameters = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, byte[]> parts = new HashMap<>();
    private String body;

    HttpRequestMock() {
    }

    public HttpRequestMock httpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public HttpRequestMock path(String path) {
        this.path = path;
        return this;
    }

    public HttpRequestMock header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public HttpRequestMock contentType(String value) {
        return header("Content-Type", value);
    }

    public HttpRequestMock withPart(String name, byte[] bytes) {
        parts.put(name, bytes);
        return this;
    }

    public HttpRequestMock body(String body) {
        this.body = body;
        return this;
    }

    public HttpTestResponse send() {
        HttpTestClient testClient = new HttpTestClient();
        return testClient.send(this);
    }

    public static void main(String[] args) {
        ControllerMethodInvoker invoker = ApplicationContext.getInstance().getBean(ControllerMethodInvoker.class);
    }

}



