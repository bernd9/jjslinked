package com.jjslinked;

public interface ReceiverInvoker {

    void onMessage(ClientMessage message, ApplicationContext applicationContext) throws Exception;
}
