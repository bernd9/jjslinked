package com.ejc.http.api.controller;

import com.ejc.ApplicationContext;
import com.ejc.Inject;
import com.ejc.InjectAll;
import com.ejc.Singleton;
import com.ejc.http.exception.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
class ControllerMethodInvoker {

    @Inject
    private ExceptionHandler exceptionHandler;

    @Inject
    private ApplicationContext context;

    @InjectAll
    private List<ControllerMethod> controllerMethods;

    void invoke(HttpServletRequest request, HttpServletResponse response) {
        try {
            doInvocation(request, response);
        } catch (Exception e) {
            exceptionHandler.handleException(e, response);
        }
    }

    private void doInvocation(HttpServletRequest request, HttpServletResponse response) {
        List<ControllerMethodInvocationContext> contexts = contextForMatchingMethods(request, response);
        switch (contexts.size()) {
            case 0:
                throw new ControllerMethodMappingException(request, "no mapping");
            case 1:
                doInvocation(contexts.get(0));
            default:
                throw new ControllerMethodMappingException(request, "ambiguous mapping");
        }
    }

    private void doInvocation(ControllerMethodInvocationContext context) {

    }


    private List<ControllerMethodInvocationContext> contextForMatchingMethods(HttpServletRequest request, HttpServletResponse response) {
        return controllerMethods.stream()//
                .filter(method -> method.httpMethodMatches(request))
                .filter(method -> method.pathMatches(request))
                .map(method -> new ControllerMethodInvocationContext(method, request, response, method.getPathVariables(request)))
                .collect(Collectors.toList());
    }

}
