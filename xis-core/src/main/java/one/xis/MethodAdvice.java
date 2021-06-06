package one.xis;

public interface MethodAdvice {

    Object execute(Object proxy, Object[] args, JoinPoint joinPoint) throws Throwable;
}
