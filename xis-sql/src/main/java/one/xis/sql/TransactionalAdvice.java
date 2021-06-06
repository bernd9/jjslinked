package one.xis.sql;

import one.xis.JoinPoint;
import one.xis.MethodAdvice;
import one.xis.sql.api.Session;

import java.lang.reflect.Method;

class TransactionalAdvice implements MethodAdvice {

    @Override
    public Object execute(Object o, Object[] objects, JoinPoint joinPoint) throws Throwable {
        boolean isSessionStarter = !Session.exists();
        boolean isTransactionStarter = false;
        Session session = Session.getInstance();
        if (!session.hasTransactionConfig()) {
            int isolationLevel = getTransactionIsolationLevel(joinPoint.getMethod());
            session.setTransactionIsolationLevel(isolationLevel);
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

    private Integer getTransactionIsolationLevel(Method method) {
        if (method.isAnnotationPresent(Transactional.class)) {
            return method.getAnnotation(Transactional.class).isolationLevel();
        }
        if (method.getDeclaringClass().isAnnotationPresent(Transactional.class)) {
            return method.getDeclaringClass().getAnnotation(Transactional.class).isolationLevel();
        }
        throw new IllegalStateException(method + ": @Transactional-annotation not found");
    }
}
