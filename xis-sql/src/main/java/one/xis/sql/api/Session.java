package one.xis.sql.api;

import com.ejc.util.ObjectUtils;
import lombok.NonNull;
import one.xis.sql.JdbcException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Session {

    private static final ThreadLocal<Session> sessions = new ThreadLocal<>();

    private final Map<Class<?>, Map<Integer,Object>> sessionEntities = new HashMap<>();
    private final ConnectionHolder connectionHolder = new ConnectionHolder();

    public static boolean start() {
        if (sessions.get() != null) {
            return false;
        }
        sessions.set(new Session());
        return true;
    }

    public static void remove() {
        sessions.remove();
    }

    public static Session getInstance() {
        return sessions.get();
    }

    public void register(Object o, UnaryOperator<Object> cloneOperator) {
        storeClone(cloneOperator.apply(o), System.identityHashCode(o));
    }

    SqlSaveAction getSaveAction(Object o, EntityFunctions functions) {
        return getRegisteredClone(o).map(clone -> getSaveAction(o, clone, functions)).orElse(SqlSaveAction.INSERT);
    }

    private SqlSaveAction getSaveAction(Object orig, Object clone, EntityFunctions functions) {
        checkPrimaryKeyUnchanged(orig, clone, functions::getPk);
        return getSaveAction(orig, clone, functions::compareColumnValues);
    }

    private SqlSaveAction getSaveAction(Object orig, Object clone, BiFunction<Object,Object, Boolean> compareFunction) {
        return compareFunction.apply(orig, clone) ? SqlSaveAction.NOOP : SqlSaveAction.UPDATE;
    }

    private void checkPrimaryKeyUnchanged(Object orig, Object clone, Function<Object, Object> getPkFunction) {
        if (ObjectUtils.equals(getPkFunction.apply(orig), getPkFunction.apply(clone))) {
            throw new IllegalStateException("primary key changed: " + orig);
        }
    }

    private void storeClone(Object clone, int hashCode) {
        sessionEntities.computeIfAbsent(clone.getClass(), c -> new HashMap<>()).put(hashCode, clone);
    }

    @SuppressWarnings("unchecked")
    private Optional<Object> getRegisteredClone(@NonNull Object orig) {
        return Optional.ofNullable(sessionEntities.getOrDefault(orig.getClass(), Collections.EMPTY_MAP).get(System.identityHashCode(orig)));
    }

    public void startTransaction(int isolationLevel) {
        try {
            getConnection().setAutoCommit(false);
            getConnection().setTransactionIsolation(isolationLevel);
        } catch (SQLException e) {
            throw new JdbcException("starting transaction failed", e);
        }
    }

    public boolean hasTransaction() {
        try {
            return !getConnection().getAutoCommit() && getConnection().getTransactionIsolation() != Connection.TRANSACTION_NONE;
        } catch (SQLException e) {
            throw new JdbcException("checking autocommit status failed", e);
        }
    }

    public void commit() {
        try {
            getConnection().commit();
        } catch (SQLException e) {
            throw new JdbcException("commit failed", e);
        }
    }

    public void close() {
        sessionEntities.clear();
        try {
            getConnection().close();
        } catch (SQLException e) {
            throw new JdbcException("closing connection failed", e);
        }
    }

    private Connection getConnection() {
        try {
            return connectionHolder.getConnection();
        } catch (SQLException e) {
            throw new JdbcException("getting connection failed", e);
        }
    }


    private static class ConnectionHolder {
        private Connection connection;

        synchronized Connection getConnection() throws SQLException {
            if (connection == null) {
                connection = DataSourceHolder.getInstance().getDataSource().getConnection();
            }
            return connection;
        }
    }
}
