package one.xis.sql;

import one.xis.JoinPoint;
import one.xis.MethodAdvice;
import one.xis.sql.api.Session;

import java.sql.Connection;

class ServiceAdvice implements MethodAdvice {

    @Override
    public Object execute(Object o, Object[] objects, JoinPoint joinPoint) throws Throwable {
        boolean isSessionStarter = !Session.exists();
        boolean isTransactionStarter = false;
        Session session = Session.getInstance();
        if (!session.hasTransactionConfig()) {
            // no @Transactional
            session.setTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
            isTransactionStarter = true;
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
}
