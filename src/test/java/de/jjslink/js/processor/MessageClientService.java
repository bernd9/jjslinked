package de.jjslink.js.processor;

import de.jjslink.annotations.ClientService;
import de.jjslink.annotations.LinkedMethod;
import de.jjslink.annotations.UserId;

@ClientService
abstract class MessageClientService {

    @LinkedMethod(clientMethod = "onMessageReceived")
    abstract void sendMessage(String message, @UserId String userId);

    @LinkedMethod(clientMethod = "sendMessage")
    void onClientMessage(String message, @UserId String userId) {

    }
}
