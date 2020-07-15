package com.jjslinked.receiver;

import com.ejc.ApplicationContext;
import com.jjslinked.ClientMessage;

public interface ReceiverInvoker {

    Object onMessage(ClientMessage message, ApplicationContext applicationContext) throws Exception;

    String getBeanClass();
}
