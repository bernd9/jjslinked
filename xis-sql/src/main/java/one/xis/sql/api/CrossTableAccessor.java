package one.xis.sql.api;

import lombok.RequiredArgsConstructor;
import one.xis.sql.JdbcException;
import one.xis.sql.api.collection.EntityCollections;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public abstract class CrossTableAccessor<EID, F, FID> extends JdbcExecutor {
    private final CrossTableStatements crossTableStatements;
    private final EntityFunctions<F, FID> fieldEntityFunctions;

    void deleteReferences(EID entityId, Stream<FID> fieldIds) {
        try (JdbcStatement st = prepare(crossTableStatements.getDeleteReferencesOfEntitySql())) {
            fieldIds.forEach(fieldId -> {
                setEntityKey(st, 1, entityId);
                setFieldKey(st, 2, fieldId);
                addBatch(st);
            });
            st.executeBatch();
        } catch (SQLException e) {
            throw new JdbcException("delete references failed", e);
        }
    }

    void addReferences(EID entityId, Stream<FID> fieldIds) {
        try (JdbcStatement st = prepare(crossTableStatements.getInsertReferencesOfEntitySql())) {
            fieldIds.forEach(fieldId -> {
                setEntityKey(st, 1, entityId);
                setFieldKey(st, 2, fieldId);
                addBatch(st);
            });
            st.executeBatch();
        } catch (SQLException e) {
            throw new JdbcException("insert references failed", e);
        }
    }


    public <C extends Collection<F>> C getJoinedFieldValues(EID key, Class<C> collectionType) {
        C collection = EntityCollections.getCollection(collectionType);
        try (JdbcStatement st = prepare(crossTableStatements.getJoinSql())) {
            st.set(1, key);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    collection.add(fieldEntityFunctions.toEntityProxy(rs));
                }
            }
        } catch (SQLException e) {
            throw new JdbcException("failed to execute select all", e);
        }
        return collection;
    }


    protected abstract void setFieldKey(JdbcStatement st, int i, FID fieldId);

    protected abstract void setEntityKey(JdbcStatement st, int i, EID entityId);

}
