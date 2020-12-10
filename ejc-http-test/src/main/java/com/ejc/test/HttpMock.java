package com.ejc.test;

import com.ejc.api.context.ApplicationContext;
import com.ejc.http.HttpMethod;
import com.ejc.http.api.controller.ControllerMethodInvoker;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Getter
public class HttpMock {
    private HttpMethod httpMethod = HttpMethod.GET;
    private String path = "";
    private final Map<String, Set<Object>> parameters = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, byte[]> parts = new HashMap<>();
    private String body;

    HttpMock() {
    }

    public HttpMock httpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public HttpMock path(String path) {
        this.path = path;
        return this;
    }

    public HttpMock header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public HttpMock contentType(String value) {
        return header("Content-Type", value);
    }

    public HttpMock withPart(String name, byte[] bytes) {
        parts.put(name, bytes);
        return this;
    }

    public HttpMock body(String body) {
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



