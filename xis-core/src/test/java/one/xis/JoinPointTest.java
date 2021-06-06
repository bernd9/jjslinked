package one.xis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JoinPointTest {

    private JoinPoint joinPoint;
    private Method method;

    @BeforeEach
    void init() throws NoSuchMethodException {
        method = Calculator.class.getDeclaredMethod("twoTimes", int.class);
    }

    @Test
    void test1() throws Throwable {
        MethodAdvice methodAdviceAdd1 = new MethodAdvice() {
            @Override
            public Object execute(Object proxy, Object[] args, JoinPoint joinPoint) throws Throwable {
                Integer arg = (Integer) args[0] + 1;
                return joinPoint.proceed(proxy, new Object[]{arg});
            }
        };

        MethodAdvice methodAdvice5Times = new MethodAdvice() {
            @Override
            public Object execute(Object proxy, Object[] args, JoinPoint joinPoint) throws Throwable {
                Integer arg = (Integer) args[0] * 5;
                return joinPoint.proceed(proxy, new Object[]{arg});
            }
        };

        JoinPoint joinPoint = JoinPoint.prepare(List.of(methodAdviceAdd1, methodAdvice5Times), method);
        int result = (int) joinPoint.proceed(new Calculator(), new Object[]{10});

        assertThat(result).isEqualTo(110);

    }

    class Calculator {
        int twoTimes(int arg) {
            return arg * 2;
        }
    }


}