package com.jjslinked;

import com.ejaf.InvokerEvent;
import com.jjslinked.model.ClientMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class IncomingMessageEvent implements InvokerEvent {
    private ClientMessage clientMessage;
}
