package com.ejc.http.api.controller;

import com.ejc.ApplicationContext;
import com.ejc.Inject;
import com.ejc.InjectAll;
import com.ejc.Singleton;
import com.ejc.http.api.HttpResponder;
import com.ejc.http.exception.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
class ControllerMethodInvoker {

    @Inject
    private ExceptionHandler exceptionHandler;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private HttpResponder responder;

    @InjectAll
    private List<ControllerMethod> controllerMethods;

    void invoke(HttpServletRequest request, HttpServletResponse response) {
        try {
            doInvocation(request, response);
        } catch (Exception e) {
            exceptionHandler.handleException(e, response);
        }
    }

    private void doInvocation(HttpServletRequest request, HttpServletResponse response) throws Exception {
        var methods = getMatchingMethods(request, response);
        switch (methods.size()) {
            case 0:
                throw new ControllerMethodMappingException(request, "no mapping");
            case 1:
                doInvocation(methods.get(0), request, response);
            default:
                throw new ControllerMethodMappingException(request, "ambiguous mapping");
        }
    }

    private void doInvocation(ControllerMethod controllerMethod, HttpServletRequest request, HttpServletResponse response) throws Exception {
        var context = createInvocationContext(controllerMethod, request, response);
        var controller = applicationContext.getBean(controllerMethod.getControllerClass());
        var method = getMethod(controller, controllerMethod);
        var parameters = getParameters(controllerMethod, context).toArray();
        var returnValue = method.invoke(controller, parameters);
        responder.sendResponse(returnValue, request, response);
    }


    private Method getMethod(Object controller, ControllerMethod controllerMethod) throws Exception {
        var types = controllerMethod.getParameterTypes().stream().toArray(size -> new Class[size]);
        var method = controller.getClass().getMethod(controllerMethod.getMethodName(), types);
        method.setAccessible(true);
        return method;
    }

    private List<Object> getParameters(ControllerMethod method, ControllerMethodInvocationContext context) {
        return method.getParameterProviders().stream()
                .map(provider -> provider.provide(context))
                .collect(Collectors.toList());
    }

    private ControllerMethodInvocationContext createInvocationContext(ControllerMethod method, HttpServletRequest request, HttpServletResponse response) {
        return ControllerMethodInvocationContext.builder()
                .applicationContext(applicationContext)
                .pathVariables(method.getPathVariables(request))
                .request(request)
                .response(response)
                .build();
    }

    private List<ControllerMethod> getMatchingMethods(HttpServletRequest request, HttpServletResponse response) {
        return controllerMethods.stream()
                .filter(method -> method.httpMethodMatches(request))
                .filter(method -> method.pathMatches(request))
                .collect(Collectors.toList());
    }
}
