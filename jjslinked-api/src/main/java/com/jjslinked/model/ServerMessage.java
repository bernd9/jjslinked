package com.jjslinked.model;

import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.Optional;

@Value
@Builder
public class ServerMessage {
    private String path;
    private Map<String,String> parameters;

}
