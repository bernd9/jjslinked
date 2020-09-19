package com.ejc.sql;

import com.ejc.Singleton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Singleton
public class TransactionStatus extends ThreadLocal<TransactionStatus.IsolationLevel> {

    public void startTransactionSensitive(Optional<Integer> isolationLevel) {
        if (get() == null) {
            set(new IsolationLevel(isolationLevel));
        }
    }

    public boolean isTransaction() {
        return get() != null;
    }

    public Optional<Integer> getIsolationLevel() {
        return get() == null ? Optional.empty() : get().getLevel();
    }

    public void endTransaction() {
        remove();
    }


    @Getter
    @RequiredArgsConstructor
    class IsolationLevel {
        private final Optional<Integer> level;
    }
}


