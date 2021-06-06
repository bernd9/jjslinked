package one.xis.http.api.controller;

public interface ParameterProvider<T> {
    T provide(ControllerMethodInvocationContext context);
}
