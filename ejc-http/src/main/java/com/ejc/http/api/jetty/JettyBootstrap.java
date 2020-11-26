package com.ejc.http.api.jetty;

import com.ejc.Configuration;
import com.ejc.Init;
import com.ejc.Value;
import com.ejc.http.api.MainServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

@Configuration
class JettyBootstrap {

    @Value(value = "http.port", defaultValue = "8080")
    private int port;

    private Server server;

    @Init
    void start() throws Exception {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(MainServlet.class, "/");
        server.setHandler(servletHandler);
        server.setConnectors(new Connector[]{connector});
        server.start();
    }

    // TODO annotation
    void destroy() {
        server.destroy();
    }

}
