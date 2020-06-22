import com.jjslinked.receiver.ReceiverInvoker;

public class Test extends ReceiverInvoker {
    public Test(Class<?> beanClass, String name, Class<?>... parameterTypes) {
        super(beanClass, name, parameterTypes);
    }
}
