package one.xis.sql.api;

import one.xis.sql.JdbcException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class JdbcExecutor {


    protected String escapedStatementParameter(Object o) {
        if (o instanceof String) {
            String s = (String) o;
            return s.replace("'", "\'").replace("\"", "\\\"");
        }
        return o.toString();
    }


    protected PreparedStatementWrapper prepare(String sql) {
        try {
            return new PreparedStatementWrapper(getConnection().prepareStatement(sql));
        } catch (SQLException e) {
            throw new JdbcException("preparing statement failed: " + sql, e);
        }
    }

    protected PreparedStatement prepare(String sql, int flags) {
        try {
            return getConnection().prepareStatement(sql, flags);
        } catch (SQLException e) {
            throw new JdbcException("preparing statement failed: " + sql, e);
        }
    }


    protected Connection getConnection() {
        return null;
    }


}
