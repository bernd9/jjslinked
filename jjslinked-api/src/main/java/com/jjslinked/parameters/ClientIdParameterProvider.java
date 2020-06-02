package com.jjslinked.parameters;

import com.jjslinked.model.ClientMessage;

public class ClientIdParameterProvider implements ParameterProvider<String> {
    @Override
    public String provide(ClientMessage clientMessage) {
        return clientMessage.getClientId().orElseThrow();
    }
}
