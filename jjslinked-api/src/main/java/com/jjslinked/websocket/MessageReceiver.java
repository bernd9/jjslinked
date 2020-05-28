package com.jjslinked.websocket;

import com.jjslinked.model.ClientMessageBuilder;
import com.jjslinked.model.ServerMessageBuilder;
import com.jjslinked.service.Invoker;
import lombok.RequiredArgsConstructor;

import java.net.http.WebSocket;

@RequiredArgsConstructor
public class MessageReceiver {

    private final ClientMessageBuilder clientMessageBuilder;
    private final ServerMessageBuilder serverMessageBuilder;
    private final MessageEmitter messageEmitter;
    private final Invoker invoker;

    // @OnMessage
    void onMessage(String message, WebSocket session) {
        Object response = invoker.invoke(clientMessageBuilder.clientMessage(message, session));
        if (!Void.TYPE.isInstance(response)) {
            messageEmitter.emit(serverMessageBuilder.build(response), session);
        }
    }
}
