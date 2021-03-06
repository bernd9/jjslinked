package one.xis.http.api;

import one.xis.Configuration;
import one.xis.Init;
import one.xis.Value;
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
