package com.jjslinked.receiver;

import com.google.common.base.Functions;
import com.ejc.*;
import com.jjslinked.ClientMessage;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class ReceiverInvokerDispatcher {

    @InjectAll
    private Set<ReceiverInvoker> invokers;

    @Inject
    private ApplicationContextBase applicationContext;

    private Map<String, ReceiverInvoker> invokersByClassName;

    @Init
    void init() {
        invokersByClassName = invokers.stream()
                .collect(Collectors.toMap(ReceiverInvoker::getBeanClass, Functions.identity()));
    }

    public Object onMessage(ClientMessage clientMessage) throws Exception {
        ReceiverInvoker invoker = invokersByClassName.get(clientMessage.getTargetClass());
        if (invoker == null) {
            throw new IllegalArgumentException("umatched target class");
        }
        return invoker.onMessage(clientMessage, applicationContext);
    }

}
