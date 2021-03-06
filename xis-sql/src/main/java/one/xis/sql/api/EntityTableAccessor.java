package one.xis.sql.api;

import one.xis.context.UsedInGeneratedCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import one.xis.sql.JdbcException;
import one.xis.sql.api.collection.EntityArrayList;
import one.xis.sql.api.collection.EntityCollections;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@RequiredArgsConstructor
public abstract class EntityTableAccessor<E, EID> extends JdbcExecutor {

    // TODO validate entity must have more then one parameter (1. is id), otherwise @CollectionTable !
    private static final String TEXT_NO_PK = "Entity has no primary key. Consider to set ";// TODO

    private final EntityStatements<E, EID> entityStatements;
    private final EntityFunctions<E, EID> entityFunctions;
    
    private final Class<E> entityType;
    @Getter
    private final Class<EID> pkType;


    public Optional<E> findById(EID id) {
        try (JdbcStatement st = prepare(entityStatements.getSelectByIdSql())) {
            setPk(st, 1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return Optional.of((E) toEntityProxy(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new JdbcException("failed to execute select by id", e);
        }
    }

    List<E> findAll() {
        EntityArrayList<E> list = new EntityArrayList<>(entityType);
        try (PreparedStatement st = prepare(entityStatements.getSelectAllSql())) {
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add((E) toEntityProxy(rs));
                }
            }
        } catch (SQLException e) {
            throw new JdbcException("failed to execute select all", e);
        }

        return list;
    }

    protected abstract void insert(E entity);

    protected abstract void insert(Collection<E> entities);

    public void update(Collection<E> updateEntities) {
        Iterator<E> entityIterator = updateEntities.iterator();
        try (JdbcStatement st = prepare(entityStatements.getUpdateSql())) {
            while (entityIterator.hasNext()) {
                E entity = entityIterator.next();
                st.clearParameters();
                entityStatements.setUpdateSqlParameters(st, entity);
                st.addBatch();
            }
            st.executeBatch();
        } catch (SQLException e) {
            throw new JdbcException("failed to execute update", e);
        }
    }

    public boolean delete(E entity) {
        return deleteById(getPk(entity));
    }

    public boolean deleteById(EID id) {
        try (JdbcStatement st = prepare(entityStatements.getDeleteSql())) {
            setPk(st, 1, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new JdbcException("failed to execute delete", e);
        }
    }

    public void delete(Collection<E> entities) {
        Iterator<E> entityIterator = entities.iterator();
        try (JdbcStatement st = prepare(entityStatements.getDeleteSql())) {
            while (entityIterator.hasNext()) {
                st.clearParameters();
                E entity = entityIterator.next();
                setPk(st, 1, getPk(entity));
                st.addBatch();
            }
            st.executeBatch();
        } catch (SQLException e) {
            throw new JdbcException("failed to execute delete", e);
        }
    }


    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public <E> Optional<E> getByColumnValue(Object columnValue, String columnName) {
        List<E> list = getListByColumnValue(columnValue, columnName);
        switch (list.size()) {
            case 0:
                return Optional.empty();
            case 1:
                return Optional.of(list.get(0));
            default:
                throw new IllegalStateException("too many results");
        }
    }

    @UsedInGeneratedCode
    public <E> List<E> getListByColumnValue(Object columnValue, String columnName) {
        return getAllByColumnValue(columnValue, columnName, List.class);
    }


    public <C extends Collection<E>> C getAllByColumnValue(Object columnValue, String columnName, Class<C> collectionType) {
        C collection = EntityCollections.getCollection(collectionType);
        try (JdbcStatement st = prepare(entityStatements.getSelectByColumnValueSql(columnName))) {
            st.set(1, columnValue);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    collection.add((E) toEntityProxy(rs));
                }
            }
        } catch (SQLException e) {
            throw new JdbcException("failed to execute select by column value", e);
        }
        return collection;
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void insertWithDbmsGeneratedKey(E entity) {
        insertWithDbmsGeneratedKeys(Collections.singletonList(entity));
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void insertWithManuallyPlacedKey(E entity) {
        insertWithManuallyPlacedKeys(Collections.singletonList(entity));
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void insertWithApiGeneratedKey(E entity) {
        insertWithApiGeneratedKeys(Collections.singletonList(entity));
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void insertWithDbmsGeneratedKeys(Collection<E> entities) {
        Iterator<E> entityIterator = entities.iterator();
        try (JdbcStatement st = prepare(entityStatements.getInsertSql())) {
            while (entityIterator.hasNext()) {
                E entity = entityIterator.next();
                st.clearParameters();
                entityStatements.setInsertSqlParameters(st, entity);
                st.addBatch();
            }
            st.executeBatch();
            ResultSet keys = st.getGeneratedKeys();
            entityIterator = entities.iterator();
            while (entityIterator.hasNext()) {
                E entity = entityIterator.next();
                if (!keys.next()) {
                    throw new JdbcException("number of keys does not match");
                }
                EID id = getPk(keys, 1);
                if (id == null) {
                    throw new JdbcException(""); // TODO
                }
                setPk(entity, id);
            }

        } catch (SQLException e) {
            throw new JdbcException("failed to execute bulk-insert", e);
        }
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void insertWithManuallyPlacedKeys(Collection<E> entities) {
        Iterator<E> entityIterator = entities.iterator();
        try (JdbcStatement st = prepare(entityStatements.getInsertSql())) {
            while (entityIterator.hasNext()) {
                E entity = entityIterator.next();
                if (getPk(entity) == null) {
                    throw new JdbcException(""); // TODO
                }
                st.clearParameters();
                entityStatements.setInsertSqlParameters(st, entity);
                st.addBatch();
            }
            st.executeBatch();
        } catch (SQLException e) {
            throw new JdbcException("failed to execute bulk-insert", e);
        }
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void insertWithApiGeneratedKeys(Collection<E> entities) {
        Iterator<E> entityIterator = entities.iterator();
        try (JdbcStatement st = prepare(entityStatements.getInsertSql())) {
            while (entityIterator.hasNext()) {
                E entity = entityIterator.next();
                setPk(entity, generateKey());
                st.clearParameters();
                entityStatements.setInsertSqlParameters(st, entity);
                st.addBatch();
            }
            st.executeBatch();
        } catch (SQLException e) {
            throw new JdbcException("failed to execute bulk-insert", e);
        }
    }


    protected E toEntityProxy(ResultSet rs) throws SQLException {
        return entityFunctions.toEntityProxy(rs);
    }

    protected abstract void setPk(JdbcStatement st, int index, EID id);

    protected abstract void setPk(E entity, EID id);

    protected abstract EID getPk(E entity);

    protected abstract EID getPk(ResultSet rs, int columnIndex) throws SQLException;

    protected abstract EID generateKey();


}
