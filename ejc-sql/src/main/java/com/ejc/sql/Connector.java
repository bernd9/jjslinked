package com.ejc.sql;

import com.ejc.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.sql.Connection;
import java.sql.SQLException;

@Singleton
public class Connector {

    private ThreadLocal<Connection> currentConnection = new ThreadLocal<>();

    Connection getCurrentConnection() {
        return currentConnection.get();
    }

    public void release(Connection con) {
        try {
            con.close();
        } catch (SQLException throwables) {

        }
        currentConnection.remove();
    }


    Connection openConnection() {
        if (currentConnection.get() != null) throw new IllegalStateException();
        Connection connection = null;
        currentConnection.set(connection);
        return connection;
    }

    @RequiredArgsConstructor
    class ConnectionWrapper implements Connection {

        @Delegate(excludes = Exclusion.class)
        private final Connection connection;
        private final Connector connector;

        @Override
        public void close() throws SQLException {
            connector.release(connection);
        }

        class Exclusion {
            public void close() throws Exception {
            }
        }

    }
}
