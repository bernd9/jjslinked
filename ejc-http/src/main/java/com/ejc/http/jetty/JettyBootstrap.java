package com.ejc.http.jetty;

import com.ejc.Init;
import com.ejc.Singleton;
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
        servletHandler.addServletWithMapping(JettyServlet.class, "/");
        server.setHandler(servletHandler);
        server.setConnectors(new Connector[]{connector});
        server.start();
    }

}
