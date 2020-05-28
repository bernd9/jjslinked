package com.jjslinked.service;

import com.jjslinked.model.ClientMessage;

public interface Invoker {

    // Return void if Method is void
    Object invoke(ClientMessage clientMessage);
}
