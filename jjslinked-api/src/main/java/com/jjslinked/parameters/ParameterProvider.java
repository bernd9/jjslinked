package com.jjslinked.parameters;

import com.jjslinked.model.ClientMessage;

public interface ParameterProvider<T> {

    T provide(ClientMessage clientMessage);
}
