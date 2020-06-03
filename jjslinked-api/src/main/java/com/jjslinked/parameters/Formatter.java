package com.jjslinked.parameters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjslinked.model.ClientMessage;
import com.jjslinked.security.AuthenticationRequiredException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Formatter {

    private final ObjectMapper objectMapper;
    private final ClientMessage clientMessage;

    public String userId() {
        return clientMessage.getUserId().orElseThrow(AuthenticationRequiredException::new);
    }

    public String clientId() {
        return clientMessage.getClientId().orElseThrow(IllegalStateException::new);
    }

    public <T> T parameter(String name, Class<T> type) {
        return null; // TODO
    }

    public static String escapeHtml(String content) {
        return content;
    }

}
