package com.ejc.http.test;

import com.ejc.http.HttpMethod;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Builder(builderClassName = "MockedRequestBuilder")
@RequiredArgsConstructor
public class MockedRequest {
    private final HttpMethod httpMethod;
    private final String path;

    @Singular
    private final Map<String, Set<Object>> parameters;

    @Singular
    private final Map<String, String> headers;

    @Singular
    private final Map<String, byte[]> parts;
    private final String body;



}



