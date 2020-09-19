package com.ejc.sql;

import com.ejc.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Singleton
public class TransactionStatus extends ThreadLocal<TransactionStatus.IsolationLevel> {

    public void startTransactionSensitive(Integer isolationLevel) {
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


    @RequiredArgsConstructor
    class IsolationLevel {
        private final Integer value;

        Optional<Integer> getLevel() {
            return Optional.ofNullable(value);
        }
    }
}


