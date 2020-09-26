package com.ejc.sql.api;

import com.ejc.Inject;
import com.ejc.Singleton;
import com.ejc.sql.Transactional;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Optional;

@Singleton
public class TransactionalMethodHandler implements InvocationHandler {

    @Inject
    private TransactionHolder transactionHolder;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Optional<Integer> isolationLevel = Optional.of(method.getAnnotation(Transactional.class).isolationLevel())
                .filter(value -> value.intValue() > -1);
        transactionHolder.startTransactionSensitive(isolationLevel);
        try {
            return method.invoke(proxy, args);
        } finally {
            transactionHolder.endTransaction();
        }
    }
}