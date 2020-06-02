package com.jjslinked.model;

import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.Optional;

@Value
@Builder
public class ClientMessage {
    private String service;
    private String path;
    private String clientId;
    private Optional<String> userId;
    private Map<String, String> parameters;
}
