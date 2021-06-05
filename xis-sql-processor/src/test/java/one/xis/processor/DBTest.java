package one.xis.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@RequiredArgsConstructor
public abstract class DBTest<C extends JdbcDatabaseContainer> {

    private final String version;
    private C container;

    @Getter
    protected Connection connection;

    protected void doInit() throws Exception {
        startContainer();
        connect();
        init(connection);
    }


    protected abstract void init(Connection con) throws SQLException;


    protected void doDestroy() throws SQLException {
        destroy(connection);
        disconnect();
        stopContainer();
    }

    protected abstract void destroy(Connection con) throws SQLException;

    private void stopContainer() {
        container.stop();
    }


    private void startContainer() {
        container = getContainer(version);
        container.start();
    }

    protected abstract C getContainer(String imageName);


    private void connect() throws SQLException {
        connection = DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
    }

    private void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }


    protected void execute(String sql) throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute(sql);
        }
    }


}
