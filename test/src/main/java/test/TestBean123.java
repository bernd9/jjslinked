package test;


import com.injectlight.Inject;
import com.injectlight.Singleton;
import com.jjslinked.generated.ReceiverInvokerRegistry;

@Singleton
public class TestBean123 {

    public static void main(String[] args) throws Exception{
        ReceiverInvokerRegistry receiverInvokerRegistry = ReceiverInvokerRegistry.getInstance();
        Object o = Class.forName("com.injectlight.ApplicationContext").newInstance();
    }
 }
