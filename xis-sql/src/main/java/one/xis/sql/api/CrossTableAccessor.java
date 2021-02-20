package one.xis.sql.api;

import lombok.RequiredArgsConstructor;
import one.xis.sql.JdbcException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("unused")
@RequiredArgsConstructor
abstract class CrossTableAccessor<EID, FID> extends JdbcExecutor {
    private final String fieldColumnName;
    private final Class<EID> entityKeyType;
    private final Class<FID> fieldKeyType;

    void removeAllReferences(EID key) {
        try (PreparedStatement st = prepare(getDeleteAllReferencesSql())) {
            setEntityKey(st, 1, key);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new JdbcException("failed to close statement");
        }
    }

    void updateByFieldReferences(EID entityKey, Set<FID> valueKeys) {
        Set<FID> fieldKeysOld = selectFieldKeys(entityKey);
        deleteObsoleteReferences(entityKey, fieldKeysOld, valueKeys);
    }

    private void insertNewReferences(EID entityId, Set<FID> fieldKeysOld, Set<FID> fieldKeysNew) {
        LinkedList<FID> toBeAdded = new LinkedList<>(fieldKeysNew);
        toBeAdded.removeAll(fieldKeysOld);
        if (!toBeAdded.isEmpty()) {
            insertNewReferences(entityId, toBeAdded);
        }
    }

    private void insertNewReferences(EID entityId, Collection<FID> newFieldReferences) {
        int index = 0;
        Iterator<FID> fieldIdFieldIterator = newFieldReferences.iterator();
        try (PreparedStatement st = prepare(getInsertReferencesSql20())) {
            setEntityKey(st, ++index, entityId);
            while (fieldIdFieldIterator.hasNext()) {
                for (int i = 0; i < 20; i++) {
                    FID fieldId = fieldIdFieldIterator.hasNext() ? fieldIdFieldIterator.next() : null;
                    setFieldKey(st, ++index, fieldId);
                }
                st.addBatch();
            }
            st.executeBatch();
        } catch (SQLException e) {
            throw new JdbcException("delete references failed ", e);
        }
    }


    private void deleteObsoleteReferences(EID entityId, Set<FID> fieldKeysOld, Set<FID> fieldKeysNew) {
        LinkedList<FID> toBeDeleted = new LinkedList<>(fieldKeysOld);
        if (toBeDeleted.retainAll(fieldKeysNew)) {
            if (!toBeDeleted.isEmpty()) {
                deleteObsoleteReferences(entityId, toBeDeleted);
            }
        }
    }

    private void deleteObsoleteReferences(EID entityId, List<FID> fieldIds) {
        int index = 0;
        Iterator<FID> fieldIdFieldIterator = fieldIds.iterator();
        try (PreparedStatement st = prepare(getDeleteReferencesSql20())) {
            setEntityKey(st, ++index, entityId);
            while (fieldIdFieldIterator.hasNext()) {
                for (int i = 0; i < 20; i++) {
                    FID fieldId = fieldIdFieldIterator.hasNext() ? fieldIdFieldIterator.next() : null;
                    setFieldKey(st, ++index, fieldId);
                }
                st.addBatch();
            }
            st.executeBatch();
        } catch (SQLException e) {
            throw new JdbcException("delete references failed ", e);
        }
    }

    private Set<FID> selectFieldKeys(EID entityId) {
        Set<FID> fieldKeys = new HashSet<>();
        try (PreparedStatement st = prepare(getSelectFieldKeysSql())) {
            setEntityKey(st, 1, entityId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    fieldKeys.add(getFieldKey(rs));
                }
            }
        } catch (SQLException e) {
            throw new JdbcException("select field-ids failed ", e);
        }
        return fieldKeys;
    }

    protected abstract String getSelectFieldKeysSql();

    // delete from [TABLE] where [key] = ?
    protected abstract String getDeleteAllReferencesSql();

    // delete from [TABLE] where [key] = ? and [fieldkey] IN (?,?,?,...)
    protected abstract String getDeleteReferencesSql20();

    protected abstract String getInsertReferencesSql20();

    protected abstract void setEntityKey(PreparedStatement st, int index, EID key) throws SQLException;

    protected abstract void setFieldKey(PreparedStatement st, int index, FID key) throws SQLException;

    protected abstract FID getFieldKey(ResultSet rs) throws SQLException;


}
