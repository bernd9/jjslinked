package one.xis.sql.api;

import one.xis.sql.JdbcException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class JdbcExecutor {

    protected JdbcStatement prepare(String sql) {
        try {
            return new JdbcStatement(getConnection().prepareStatement(sql));
        } catch (SQLException e) {
            throw new JdbcException("preparing statement failed: " + sql, e);
        }
    }

    protected JdbcStatement prepare(String sql, int flags) {
        try {
            return new JdbcStatement(getConnection().prepareStatement(sql, flags));
        } catch (SQLException e) {
            throw new JdbcException("preparing statement failed: " + sql, e);
        }
    }

    protected void addBatch(PreparedStatement st) {
        try {
            st.addBatch();
        }
        catch (SQLException e) {
            throw new JdbcException("add to batch failed", e);
        }
    }

    protected Connection getConnection() {
        return Session.getInstance().getConnection();
    }

}
