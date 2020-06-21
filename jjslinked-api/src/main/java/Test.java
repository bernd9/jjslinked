import com.jjslinked.ReceiverInvoker;

public class Test extends ReceiverInvoker {
    public Test(Class<?> beanClass, String name, Class<?>... parameterTypes) {
        super(beanClass, name, parameterTypes);
    }
}
