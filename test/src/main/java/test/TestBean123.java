package test;


import com.injectlight.Inject;
import com.injectlight.Singleton;
import com.jjslinked.generated.ReceiverInvokerRegistry;
import com.jjslinked.receiver.ReceiverInvokerDispatcher;

@Singleton
public class TestBean123 {

    public static void main(String[] args) throws Exception{
        ReceiverInvokerDispatcher receiverInvokerRegistry = ReceiverInvokerRegistry.getInstance();
        Object o = Class.forName("com.injectlight.ApplicationContext").newInstance();
    }
 }
