package com.ejc.sql;

import com.ejc.Inject;
import com.ejc.Singleton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Delegate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Singleton
public class Connector extends ThreadLocal<Connector.RootConnection> {

    @Inject
    private DataSource dataSource;

    @Inject
    private TransactionStatus transactionStatus;


    public Connection getConnection() throws SQLException {
        RootConnection wrapper = get();
        if (wrapper == null) {
            RootConnection rootConnection = new RootConnection(openConnection());
            if (transactionStatus.isTransaction()) {
                rootConnection.startTransaction(transactionStatus.getIsolationLevel());
            }
            set(rootConnection);
            return rootConnection;
        }
        RootConnection rootConnection = wrapper;
        return rootConnection.getChildConnection();
    }

    private Connection openConnection() {
        return null;
    }


    @Getter
    @Setter
    static class RootConnection implements Connection {

        @Delegate(excludes = CloseExclusion.class)
        private final Connection connection;
        private boolean transaction;
        private Optional<Integer> isolationLevel;
        private ChildConnection childConnection;

        RootConnection(Connection connection) {
            this.connection = connection;
        }

        ChildConnection getChildConnection() {
            if (childConnection == null) {
                childConnection = new ChildConnection(connection);
            }
            return childConnection;
        }

        @Override
        public void close() throws SQLException {
            if (transaction) {
                connection.commit();
            }
            connection.close();
        }

        void startTransaction(Optional<Integer> isolationLevel) throws SQLException {
            isolationLevel.ifPresent(this::setIsolationLevel);
            connection.setAutoCommit(false);
        }

        private void setIsolationLevel(int isolationLevel) {
            try {
                connection.setTransactionIsolation(isolationLevel);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }


    }

    @RequiredArgsConstructor
    static class ChildConnection implements Connection {

        @Delegate(excludes = CloseExclusion.class)
        private final Connection connection;

        @Override
        public void close() throws SQLException {
            // Do Nothing
        }
    }

    class CloseExclusion {
        public void close() throws Exception {
        }
    }
}
