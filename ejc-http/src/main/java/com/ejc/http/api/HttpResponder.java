package com.ejc.http.api;

import com.ejc.Inject;
import com.ejc.Singleton;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class HttpResponder {

    @Inject
    private ObjectMapper objectMapper;

    public void sendResponse(Object entity, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (entity != null) {
            objectMapper.writeValue(response.getOutputStream(), entity);
        }
    }
}
