package one.xis.sql;

import com.ejc.JoinPoint;
import com.ejc.MethodAdvice;
import one.xis.sql.api.Session;

import java.lang.reflect.Method;
import java.sql.Connection;

class TransactionalAdvice implements MethodAdvice {

    @Override
    public Object execute(Object o, Object[] objects, JoinPoint joinPoint) throws Throwable {
        boolean isSessionStarter = !Session.exists();
        boolean isTransactionStarter = false;
        Session session = Session.getInstance();
        if (!session.hasTransaction()) {
            int isolationLevel = getTransactionIsolationLevel(joinPoint.getMethod());
            if (isolationLevel != Connection.TRANSACTION_NONE) {
                session.startTransaction(isolationLevel);
                isTransactionStarter = true;
            }
        }
        try {
            return joinPoint.proceed(o, objects);
        } finally {
            try {
                if (isTransactionStarter) {
                    session.commit();
                    session.endTransaction();
                }
            } finally {
                if (isSessionStarter) {
                    session.close();
                }
            }
        }
    }

    private Integer getTransactionIsolationLevel(Method method) {
        if (method.isAnnotationPresent(Transactional.class)) {
            return method.getAnnotation(Transactional.class).isolationLevel();
        }
        if (method.getDeclaringClass().isAnnotationPresent(Transactional.class)) {
            return method.getDeclaringClass().getAnnotation(Transactional.class).isolationLevel();
        }
        throw new IllegalStateException();
    }
}
