package com.ejc.http.api.controller;

import com.ejc.Inject;
import com.ejc.Singleton;
import one.xis.context.ApplicationContext;
import com.ejc.http.api.HttpResponder;
import com.ejc.http.exception.ExceptionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class ControllerMethodInvoker {

    @Inject
    private ExceptionController exceptionController;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private HttpResponder responder;

    @Inject
    private List<ControllerMethod> controllerMethods;

    public void invoke(HttpServletRequest request, HttpServletResponse response) {
        try {
            doInvocation(request, response);
        } catch (Exception e) {
            exceptionController.handleException(e, response);
        }
    }

    private void doInvocation(HttpServletRequest request, HttpServletResponse response) throws Exception {
        doInvocation(getMatchingMethod(request), request, response);
    }

    private void doInvocation(ControllerMethod controllerMethod, HttpServletRequest request, HttpServletResponse response) throws Exception {
        var context = createInvocationContext(controllerMethod, request, response);
        var controller = applicationContext.getBean(controllerMethod.getControllerClass().getReferencedClass());
        var method = getMethod(controller, controllerMethod);
        var parameters = getParameters(controllerMethod, context).toArray();
        var returnValue = method.invoke(controller, parameters);
        closeCloseableParameters(parameters);
        responder.sendResponse(returnValue, response);
    }

    private void closeCloseableParameters(Object[] parameters) {
        Arrays.stream(parameters)
                .filter(Closeable.class::isInstance)
                .map(Closeable.class::cast)
                .forEach(closeable -> {
                    try {
                        closeable.close();
                    } catch (IOException e) {
                    }
                });
    }


    private Method getMethod(Object controller, ControllerMethod controllerMethod) throws Exception {
        var types = controllerMethod.getParameterTypes().stream().toArray(size -> new Class[size]);
        var cl = controller.getClass();
        while (cl != null && !cl.equals(Object.class)) {
            try { // TODO schöner machen
                var method = controller.getClass().getDeclaredMethod(controllerMethod.getMethodName(), types);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException e) {
                cl = cl.getSuperclass();
            }
        }
        throw new NoSuchMethodException(controllerMethod.getMethodName());
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

    private ControllerMethod getMatchingMethod(HttpServletRequest request) {
        for (ControllerMethod method : controllerMethods) {
            if (!method.httpMethodMatches(request)) {
                continue;
            }
            if (!method.pathMatches(request)) {
                continue;
            }
            return method;
        }
        throw new ControllerMethodMappingException(request, "no mapping");
    }
}
