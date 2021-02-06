package one.xis.sql;

import java.sql.SQLException;

public class JdbcException extends RuntimeException {
    public JdbcException(String message) {
        super(message);
    }

    public JdbcException(String message, SQLException cause) {
        super(message, cause);
    }
}
