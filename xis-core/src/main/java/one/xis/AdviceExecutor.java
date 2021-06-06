package one.xis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Getter
@RequiredArgsConstructor
public class AdviceExecutor {

    private AdviceExecutor next;
    private final InvocationHandler invocationHandler;

    public void addInvocationHandler(InvocationHandler invocationHandler) {
        if (next == null) {
            next = new AdviceExecutor(invocationHandler);
        } else {
            next.addInvocationHandler(invocationHandler);
        }
    }

    public void test() {
        // System.out.println("Huhu !");
    }


    private Object execute(Object proxy, Method method, Object[] args) throws Throwable {
        if (next == null) {
            return method.invoke(proxy, args);
        }
        Object obj = invocationHandler;
        Method currentMethod = invocationHandler.getClass().getMethod("invoke", Object.class, Object[].class);
        return null;

    }

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = new AdviceExecutor(null).getClass().getDeclaredMethod("test");
        m.invoke(new AdviceExecutor(null));
    }


}
