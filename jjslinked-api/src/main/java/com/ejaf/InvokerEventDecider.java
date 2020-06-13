package com.ejaf;

import com.jjslinked.MethodContext;

public interface InvokerEventDecider<E extends InvokerEvent> {

    boolean shouldInvoke(MethodContext context, E event);

}
