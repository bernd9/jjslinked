package one.xis.sql.api;

import lombok.RequiredArgsConstructor;
import one.xis.sql.JdbcException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public abstract class CrossTableAccessor<EID, FID> extends JdbcExecutor {
    private final CrossTableStatements crossTableStatements;

    void deleteReferences(EID entityId, Stream<FID> fieldIds) {
        try (PreparedStatement st = prepare(crossTableStatements.getDeleteReferencesOfEntitySql())) {
           fieldIds.forEach(fieldId -> {
               setEntityKey(st, 1, entityId);
               setFieldKey(st, 2, fieldId);
               addBatch(st);
            });
            st.executeBatch();
        } catch (SQLException e) {
            throw new JdbcException("delete references failed ", e);
        }
    }

    void addReferences(EID entityId, Stream<FID> fieldIds) {
        try (PreparedStatement st = prepare(crossTableStatements.getInsertReferencesOfEntitySql())) {
            fieldIds.forEach(fieldId -> {
                setEntityKey(st, 1, entityId);
                setFieldKey(st, 2, fieldId);
                addBatch(st);
            });
            st.executeBatch();
        } catch (SQLException e) {
            throw new JdbcException("insert references failed ", e);
        }
    }

    protected abstract void setFieldKey(PreparedStatement st, int i, FID fieldId);

    protected abstract void setEntityKey(PreparedStatement st, int i, EID entityId);

}
