package com.ejc.sql.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
class Transaction {
    private final Optional<Integer> isolationLevel;
    private Connection connection;

    Connection bind(Connection connection) throws SQLException {
        this.connection = connection;
        disableAutoCommit();
        isolationLevel.ifPresent(this::setIsolationLevel);
        return connection;
    }

    private void setIsolationLevel(int level) {
        try {
            this.connection.setTransactionIsolation(level);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void disableAutoCommit() {
        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
