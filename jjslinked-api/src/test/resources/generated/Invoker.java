import javax.naming.Context;

class Invoker {

    private Context context;

    Object invoke(ClientRequest request) {
        switch(request.getPath()) {
            case "chatService.sendMessage": // Methodenname vom Client
                ChatService chatService = (ChatService) context.getBean(ChatService.class);
                messagePublisher.publishReponse(chatService.onMessageReceived(request.getParameter("message"), parse(request.getUserId().orElseThrow(Security::exception), Integer.class)), request.getClientId())
                return Void.TYPE;
        }
    }
}