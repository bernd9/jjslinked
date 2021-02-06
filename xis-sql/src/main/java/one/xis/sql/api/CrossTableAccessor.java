package one.xis.sql.api;

import lombok.RequiredArgsConstructor;
import one.xis.sql.JdbcException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

// TODO generate 2x
@RequiredArgsConstructor
abstract class CrossTableAccessor<K1, K2> {

    private final String tableName;
    private final String columnName;
    private final Class<K1> keyType1;
    private final Class<K2> keyType2;
    private PreparedStatement removeReferencesStatement;

    void removeReferences(K1 key) {
        try (PreparedStatement st = getRemoveReferencesStatement()) {

        } catch (SQLException e) {
            throw new JdbcException("failed to close statement");
        }
    }

    void replaceValues(K1 entityKey, Collection<K2> valueKeys) {
        
    }

    private PreparedStatement getRemoveReferencesStatement() {
        try {
            return getConnection().prepareStatement(getRemoveReferencesSql());
        } catch (SQLException e) {
            throw new JdbcException("unable to prepare " + getRemoveReferencesSql(), e);
        }
    }

    abstract String getRemoveReferencesSql();

    private Connection getConnection() {
        return null;
    }


}
