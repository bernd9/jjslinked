package one.xis.sql.api;

import com.ejc.api.context.ApplicationContext;
import com.ejc.util.ObjectUtils;
import lombok.NonNull;
import one.xis.sql.JdbcException;

import javax.sql.DataSource;
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

    private static final ThreadLocal<Session> sessions = new ThreadLocal();

    private final Map<Class<?>, Map<Integer, Object>> sessionEntities = new HashMap<>();
    private final ConnectionHolder connectionHolder = new ConnectionHolder();
    private Integer transactionIsolationLevel;

    public static boolean exists() {
        return sessions.get() != null;
    }

    public static Session getInstance() {
        if (sessions.get() == null) {
            sessions.set(new Session());
        }
        return sessions.get();
    }

    public void register(Object o, UnaryOperator<Object> cloneOperator) {
        storeClone(cloneOperator.apply(o), System.identityHashCode(o));
    }

    EntityState getEntityState(Object o, EntityFunctions functions) {
        return getRegisteredClone(o).map(clone -> getEntityState(o, clone, functions)).orElse(EntityState.NEW);
    }

    private EntityState getEntityState(Object orig, Object clone, EntityFunctions functions) {
        checkPrimaryKeyUnchanged(orig, clone, functions::getPk);
        return getEntityState(orig, clone, functions::compareColumnValues);
    }

    private EntityState getEntityState(Object orig, Object clone, BiFunction<Object, Object, Boolean> compareFunction) {
        return compareFunction.apply(orig, clone) ? EntityState.UNCHANGED : EntityState.EDITED;
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

    public void setTransactionIsolationLevel(int isolationLevel) {
        transactionIsolationLevel = isolationLevel;
        if (isolationLevel != Connection.TRANSACTION_NONE) {
            try {
                getConnection().setAutoCommit(false);
                getConnection().setTransactionIsolation(isolationLevel);
            } catch (SQLException e) {
                throw new JdbcException("starting transaction failed", e);
            }
        }
    }

    public boolean hasTransactionConfig() {
        return transactionIsolationLevel != null;
    }

    public void commit() {
        try {
            getConnection().commit();
        } catch (SQLException e) {
            throw new JdbcException("commit failed", e);
        }
    }

    public void close() {
        sessions.remove();
        try {
            connectionHolder.closeConnection();
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

    public void endTransaction() {
        try {
            getConnection().setAutoCommit(true);
        } catch (SQLException e) {
            throw new JdbcException("getting connection failed", e);
        }
    }

    private static class ConnectionHolder {
        private Connection connection;
        private static DataSource dataSource;

        synchronized Connection getConnection() throws SQLException {
            if (connection == null) {
                connection = getDataSource().getConnection();
            }
            return connection;
        }

        synchronized void closeConnection() throws SQLException {
            try {
                connection.close();
            } finally {
                connection = null;
            }
        }

        private DataSource getDataSource() {
            if (dataSource == null) {
                dataSource = ApplicationContext.getInstance().getBean(DataSource.class);
            }
            return dataSource;
        }
    }
}