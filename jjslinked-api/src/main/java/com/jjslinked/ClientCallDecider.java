package com.jjslinked;

import com.ejaf.InvokerEventDecider;
import com.ejaf.MethodContext;
import com.jjslinked.model.ClientMessage;

public class ClientCallDecider implements InvokerEventDecider<IncomingMessageEvent> {

    @Override
    public boolean invoke(MethodContext context, IncomingMessageEvent event) {
        ClientMessage clientMessage = event.getClientMessage();
        return clientMessage.getTargetClass().equals(context.getClassName()) && clientMessage.getMethodSignature().equals(context.getSignature());
    }
}
