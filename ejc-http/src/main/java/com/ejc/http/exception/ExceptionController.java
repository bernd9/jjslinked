package com.ejc.http.exception;

import com.ejc.Inject;
import com.ejc.Singleton;
import com.ejc.http.api.HttpResponder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletResponse;

@Singleton
public class ExceptionController {

    @Inject
    private HttpResponder responder;

    public void handleException(Exception e, HttpServletResponse response) {
        if (e instanceof HttpStatusException) {
            response.setStatus(((HttpStatusException) e).getStatus());
        }
        responder.sendResponse(new Error(e.getMessage()), response);
    }

    @Getter
    @RequiredArgsConstructor
    class Error {
        private final String message;
    }
}


