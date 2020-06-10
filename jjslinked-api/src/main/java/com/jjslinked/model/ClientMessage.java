package com.jjslinked.model;

import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.Optional;

@Value
@Builder
public class ClientMessage {
    private String targetClass;
    private String methodSignature;
    private Optional<String> clientId;
    private Optional<String> userId;
    private Map<String, String> parameters;

    public String getParameter(String name) {
        return parameters.get(name);
    }
}
