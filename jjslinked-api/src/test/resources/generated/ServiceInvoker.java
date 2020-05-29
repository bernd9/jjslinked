import javax.naming.Context;

class ServiceInvoker {

    private Context context;

    private ChatService$$Invoker chatServiceInvoker;
    private WeatherService$$Invoker weatherServiceInvoker;

    void init(Context context) {
        this.chatServiceInvoker = new ChatServiceInvoker(context.getBean(ChatService.class));
        this.weatherServiceInvoker = new WeatherServiceInvoker(context.getBean(WeatherService.class));
    }

    Object invoke(ClientRequest request) {
        switch(Services.valueOf(request.getServiceId())) {
            case chatService:
               return chatServiceInvoker.invoke(request);
            case weatherSerive:
                return chatServiceInvoker.invoke(request);
        }
    }
}