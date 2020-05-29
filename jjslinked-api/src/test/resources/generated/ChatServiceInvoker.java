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


    private class SendMessageProxy {

        private ChatService chatService;

        void invoke(ClientRequest request) {

        }

        void before(ClientRequest request) {
            // e.g. security check
        }

        private ParamMessage {

            void prepare(ClientRequest request) {
                return validate(converter.convert(request.getParameter("message"), String.class));
            }

            private String convert(String praram) {

            }

            // Noramally not String
            private String validate(String value) {

            }

        }

        private String prepareMessage(ClientRequest request) {
            return validate(converter.convert(request.getParameter("message"), String.class));
        }

        private String prepareUserId(ClientRequest request) {
            return validate(converter.convert(features.getParameterFeature(UserId.class).apply(request), String.class));
        }

        private Message prepareMessage()

    }

}