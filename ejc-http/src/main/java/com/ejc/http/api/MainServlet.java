package com.ejc.http.api;

import com.ejc.ApplicationContext;
import com.ejc.http.api.controller.ControllerMethodInvoker;
import com.ejc.util.InstrumentationHook;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MainServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ControllerMethodInvoker invoker = ApplicationContext.getInstance().getBean(ControllerMethodInvoker.class);
        invoker.invoke(req, resp);
    }


    public static void main(String[] args) {
        System.out.println(InstrumentationHook.getInstrumentation());

    }
}
