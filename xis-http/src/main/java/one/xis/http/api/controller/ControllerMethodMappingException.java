package one.xis.http.api.controller;

import lombok.Getter;

import javax.servlet.http.HttpServletRequest;

@Getter
public class ControllerMethodMappingException extends RuntimeException {
    private final HttpServletRequest request;

    public ControllerMethodMappingException(HttpServletRequest request, String message) {
        super(message);
        this.request = request;
    }
}
