package com.ejc.http.api.jetty;

import com.ejc.Init;
import com.ejc.Singleton;
import com.ejc.http.api.MainServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

@Singleton
class JettyBootstrap {

    private Server server;

    @Init
    void start() throws Exception {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8090);
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(MainServlet.class, "/");
        server.setHandler(servletHandler);
        server.setConnectors(new Connector[]{connector});
        server.start();
    }

}
