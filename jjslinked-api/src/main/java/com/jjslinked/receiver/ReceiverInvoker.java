package com.jjslinked.receiver;

import com.injectlight.ApplicationContextBase;
import com.jjslinked.ClientMessage;

public interface ReceiverInvoker {

    Object onMessage(ClientMessage message, ApplicationContextBase applicationContext) throws Exception;

    String getBeanClass();
}
