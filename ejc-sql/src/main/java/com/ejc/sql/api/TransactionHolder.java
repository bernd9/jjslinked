package com.ejc.sql.api;

import com.ejc.Singleton;

import java.sql.SQLException;
import java.util.Optional;

@Singleton
public class TransactionHolder extends ThreadLocal<Transaction> {

    public void startTransactionSensitive(Optional<Integer> isolationLevel) {
        if (get() == null) {
            set(new Transaction(isolationLevel));
        }
    }

    public void endTransaction() {
        if (get().getConnection() != null) {
            try {
                get().getConnection().commit();
                get().getConnection().close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        remove();
    }

}


