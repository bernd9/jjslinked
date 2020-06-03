package test;

import com.jjslinked.annotations.Client;
import com.jjslinked.annotations.LinkedMethod;
import com.jjslinked.annotations.UserId;
import com.jjslinked.annotations.Validator;
import com.jjslinked.model.InvocationType;

@Client("messageService2")
public abstract class MessageService2 {

    private int i;
    @LinkedMethod("onMessage")
    abstract void sendMessage(@Validator String content, @UserId String userId);

    @LinkedMethod("sendMessage")
    void onMessageReceived(String content, @UserId String userId) {

    }
}
