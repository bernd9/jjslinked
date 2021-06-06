package one.xis.http.api.controller;

import javax.servlet.ServletRequest;

public class ParameterProviderForServletRequest implements ParameterProvider<ServletRequest> {
    @Override
    public ServletRequest provide(ControllerMethodInvocationContext context) {
        return context.getRequest();
    }
}
