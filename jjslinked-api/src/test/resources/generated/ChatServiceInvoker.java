class ChatServiceInvoker {

    private Feature features;
    private Converter converter;
    private Mapper mapper;
    private ChatService chatService;
    private Method sendMessage;

    ChatServiceInvoker(ChatService chatService) {

    }

    enum Methods {
        sendMessage
    }

    void invoke(ClientRequest request) {
        switch(Methods.valuOf(request.getMethodId())) {
            case sendMessage: return this.sendMessage.invoke(this.chatService, )
        }
    }


    private class OnMessageMethodProxy {

        private ChatService chatService;

        void invoke(ClientRequest request) {
            chatService.onMessage(paramMessage.prepare(request), paramUserId.prepare(request))
        }

        void before(ClientRequest request) {
            // e.g. security check
        }

        private ParamMessage {

            String prepare(ClientRequest request) {
                return validate(converter.convert(request.getParameter("message"), String.class));
            }

            // Noramally not String
            private String validate(String value) {

            }

        }

    }

}