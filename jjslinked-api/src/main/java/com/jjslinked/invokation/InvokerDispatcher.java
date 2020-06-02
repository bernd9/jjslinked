package com.jjslinked.invokation;

import com.jjslinked.model.ClientMessage;

public interface InvokerDispatcher {

    void dispatch(ClientMessage clientMessage) throws Exception;
}
