package com.ejaf;

public interface InvokerEventDecider<E extends InvokerEvent> {

    boolean shouldInvoke(MethodContext context, E event);

}
