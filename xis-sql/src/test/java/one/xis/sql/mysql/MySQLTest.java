package one.xis.sql.mysql;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.MySQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@RequiredArgsConstructor
abstract class MySQLTest {

    private final String version;
    private MySQLContainer container;

    @Getter
    protected Connection connection;

    @BeforeAll
    final void init() throws Exception {
        startContainer(version);
        connect();
        init(connection);
    }


    protected abstract void init(Connection con) throws SQLException;


    @AfterAll
    final void destroy() throws SQLException {
        destroy(connection);
        disconnect();
        stopContainer();
    }

    protected abstract void destroy(Connection con) throws SQLException;

    private void stopContainer() {
        container.stop();
    }


    private void startContainer(String version) throws ClassNotFoundException {
        container = new MySQLContainer("mysql:" + version);
        container.start();
        Class.forName("com.mysql.jdbc.Driver");
    }


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
