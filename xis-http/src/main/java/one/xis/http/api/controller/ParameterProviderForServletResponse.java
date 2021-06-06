package one.xis.http.api.controller;

import javax.servlet.ServletResponse;

public class ParameterProviderForServletResponse implements ParameterProvider<ServletResponse> {
    @Override
    public ServletResponse provide(ControllerMethodInvocationContext context) {
        return context.getResponse();
    }
}
