package com.jjslinked.parameters;

import com.jjslinked.model.ClientMessage;

public class UserIdParameterProvider implements ParameterProvider<String> {
    @Override
    public String provide(ClientMessage clientMessage) {
        return clientMessage.getUserId().orElseThrow();
    }
}
