package one.xis.sql;

import one.xis.sql.api.Session;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

// TODO : Big refactoring - move all from api-package to upper level after splitting api/processor an remove unnecessary "public" modifiers.
class SessionInvocationHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        boolean isOpener = Session.start();
        try {
            return doInvoke(proxy, method, args);
        } finally {
            if (isOpener) {
                Session.remove();
            }
        }
    }

    private Object doInvoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Session.getInstance().hasTransaction()) {
            return doInvokeDefault(proxy, method, args);
        }
        Integer isolationLevel = getTransactionIsolationLevel(method);
        if (isolationLevel == null || isolationLevel == Connection.TRANSACTION_NONE) {
            return doInvokeDefault(proxy, method, args);
        }
        return doInvokeInTransaction(proxy, method, args, isolationLevel);
    }

    private Object doInvokeDefault(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(proxy, args);
    }

    private Object doInvokeInTransaction(Object proxy, Method method, Object[] args, int isolationLevel) throws Throwable {
        Session session = Session.getInstance();
        session.startTransaction(isolationLevel);
        try {
            Object rv =  method.invoke(proxy, args);
            session.commit();
            return rv;
        } finally {
            session.close();
        }
    }

    private Integer getTransactionIsolationLevel(Method method) {
        if (method.isAnnotationPresent(Transactional.class)) {
            return method.getAnnotation(Transactional.class).isolationLevel();
        }
        if (method.getDeclaringClass().isAnnotationPresent(Transactional.class)) {
            return method.getDeclaringClass().getAnnotation(Transactional.class).isolationLevel();
        }
        return null;
    }
}
