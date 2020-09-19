package com.ejc.sql.api;

import com.ejc.Inject;
import com.ejc.Singleton;
import com.ejc.sql.Connector;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Singleton
public class TransactionAspect implements InvocationHandler {

    @Inject
    private Connector connector;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (get() == null) {
            set(connector.getConnection());
            get().setAutoCommit(false);
            try {
                Object rv = method.invoke(proxy, args);
                get().commit();
                return rv;
            } finally {
                connector.release(get());
                remove();
            }
        } else {

        }

        return null;
    }
}
