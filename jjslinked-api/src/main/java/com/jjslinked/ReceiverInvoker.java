package com.jjslinked;

import com.jjslinked.model.ClientMessage;

public interface ReceiverInvoker {

    void onMessage(ClientMessage message, ApplicationContext applicationContext) throws Exception;
}
