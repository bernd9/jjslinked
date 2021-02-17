package one.xis.sql.api;

import one.xis.sql.JdbcException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class JdbcExecutor {

    
    PreparedStatement prepare(String sql) {
        try {
            return getConnection().prepareStatement(sql);
        } catch (SQLException e) {
            throw new JdbcException("preparing statement failed: " + sql, e);
        }
    }


    protected Connection getConnection() {
        return null;
    }


}
