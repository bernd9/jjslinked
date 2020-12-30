package com.ejc.sql.api;

import com.ejc.Inject;
import com.ejc.Singleton;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Singleton
public class Connector {

    @Inject
    private DataSource dataSource;

    @Inject
    private TransactionHolder transactionHolder;

    public Connection getConnection() throws SQLException {
        Transaction transaction = transactionHolder.get();
        if (transaction != null) {
            return getConnection(transaction);
        }
        return openConnection();
    }

    private Connection getConnection(Transaction transaction) throws SQLException {
        if (transaction.getConnection() == null) {
            transaction.bind(openConnection());
        }
        return transaction.getConnection();
    }

    private Connection openConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
