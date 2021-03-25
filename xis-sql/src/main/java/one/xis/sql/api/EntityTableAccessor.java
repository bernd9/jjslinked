package one.xis.sql.api;

import com.ejc.api.context.UsedInGeneratedCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import one.xis.sql.JdbcException;
import one.xis.sql.api.collection.EntityArrayList;
import one.xis.sql.api.collection.EntityCollection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
@RequiredArgsConstructor
public abstract class EntityTableAccessor<E, EID> extends JdbcExecutor {

    // TODO validate entity must have more then one parameter (1. is id), otherwise @CollectionTable !
    private static final String TEXT_NO_PK = "Entity has no primary key. Consider to set ";// TODO

    private final EntityStatements<E, EID> entityStatements;

    @Getter
    private final Class<EID> pkType;

    Optional<E> getById(EID id) {
        try (PreparedEntityStatement st = prepare(entityStatements.getSelectByIdSql())) {
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
        EntityArrayList<E> list = new EntityArrayList<>();
        findAll(list);
        return list;
    }

    private void findAll(Collection<E> coll) {
        try (PreparedStatement st = prepare(entityStatements.getSelectAllSql())) {
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    coll.add((E) toEntityProxy(rs));
                }
            }
        } catch (SQLException e) {
            throw new JdbcException("failed to execute select all", e);
        }
    }

    protected abstract void insert(E entity);

    protected abstract void insert(Collection<E> entities);

    public void save(E entity) {
        if (entity instanceof EntityProxy) {
            update(Collections.singletonList((EntityProxy<E, EID>) entity));
        } else if (getPk(entity) != null){
            insert(entity);
        }
    }


    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public Collection<E> save(Collection<E> entities) {
        if (entities instanceof EntityCollection) {
            if (!((EntityCollection<?>) entities).isDirty()) {
                return entities;
            }
        }
        Collection<E> rv;
        if (entities instanceof ArrayList) {
            rv = new EntityArrayList<>();
            save(entities, rv);
        } else if (entities instanceof List) {
            rv = new EntityArrayList<>();
            save(entities, rv);
        } else {
            // TODO more collection types
            throw new IllegalArgumentException();
        }
        return rv;
    }

    private void save(Collection<E> entities, Collection<E> saved) {
        List<EntityProxy<E,EID>> proxiesForUpdate = new ArrayList<>();
        List<E> entitiesForInsert = new ArrayList<>();
        Iterator<E> entityIterator = entities.iterator();
        while (entityIterator.hasNext()) {
            E entity = entityIterator.next();
            EntityProxy<E,EID> entityProxy;
            if (entity instanceof EntityProxy) {
                entityProxy = (EntityProxy) entity;
                if (entityProxy.dirty()) {
                    proxiesForUpdate.add(entityProxy);
                }
                saved.add(entity);
            } else {
                entitiesForInsert.add(entity);
                saved.add(entity);
            }
        }
        updateProxies(proxiesForUpdate);
        insert(entitiesForInsert);
    }

    protected void updateProxies(List<EntityProxy<E,EID>> entityProxies) {
        update(entityProxies);
    }


    private void update(List<EntityProxy<E,EID>> entityProxies) {
        Set<EntityProxy<E,EID>> failedEntities = new HashSet<>();
        Iterator<EntityProxy<E,EID>> entityProxyIterator = entityProxies.iterator();
        try (PreparedEntityStatement st = prepare(entityStatements.getUpdateSql())) {
            while (entityProxyIterator.hasNext()) {
                EntityProxy<E,EID> proxy = entityProxyIterator.next();
                if (proxy.pk() == null) {
                    throw new IllegalStateException();
                }
                E entity = proxy.entity();
                st.clearParameters();
                entityStatements.setUpdateSqlParameters(st, entity);
                st.addBatch();
                proxy.doSetClean();
            }
            int[] result = st.executeBatch();
            for (int index = 0; index < result.length; index++) {
                if (result[index] == 0) {
                    failedEntities.add(entityProxies.get(index));
                }
            }
            //return failedEntities;
        } catch (SQLException e) {
            throw new JdbcException("failed to execute update", e);
        }
    }

    public boolean delete(E entity) {
        return deleteById(getPk(entity));
    }

    public boolean deleteById(EID id) {
        try (PreparedEntityStatement st = prepare(entityStatements.getDeleteSql())) {
            setPk(st, 1, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new JdbcException("failed to execute delete", e);
        }
    }

    public void delete(Collection<E> entities) {
        Iterator<E> entityIterator = entities.iterator();
        try (PreparedEntityStatement st = prepare(entityStatements.getDeleteSql())) {
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



    public void deleteAllById(Collection<EID> ids) {
        // TODO
        throw new AbstractMethodError();
    }

    public void updateColumnValuesToNull(Collection<EID> pks, String fkColumnName) {
        Iterator<EID> entityIterator = pks.iterator();
        try (PreparedEntityStatement st = prepare(entityStatements.getUpdateColumnValuesToNullByPkSql(fkColumnName))) {
            while (entityIterator.hasNext()) {
                st.clearParameters();
                EID pk = entityIterator.next();
                setPk(st, 1, pk);
                st.addBatch();
            }
            st.executeBatch();
        } catch (SQLException e) {
            throw new JdbcException("failed to execute delete", e);
        }
    }


    public Collection<EID> getPksByColumnValue(Object columnValue, String columnName) {
        List<EID> list = new ArrayList<>();
        try (PreparedEntityStatement st = prepare(entityStatements.getPksByColumnValueSql(columnName))) {
            st.set(1, columnValue);
            st.addBatch();
            ExtendedResultSet rs = new ExtendedResultSet(st.executeQuery());
            while (rs.next()) {
                list.add(rs.get(1, pkType));
            }
            return list;

        } catch (SQLException e) {
            throw new JdbcException("failed to execute delete", e);
        }
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
        try (PreparedEntityStatement st = prepare(entityStatements.getInsertSql())) {
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
        try (PreparedEntityStatement st = prepare(entityStatements.getInsertSql())) {
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
        try (PreparedEntityStatement st = prepare(entityStatements.getInsertSql())) {
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


    protected abstract EntityProxy<E,EID> toEntityProxy(ResultSet rs) throws SQLException;

    protected abstract void setPk(PreparedEntityStatement st, int index, EID id);

    protected abstract void setPk(E entity, EID id);

    protected abstract EID getPk(E entity);

    protected abstract EID getPk(ResultSet rs, int columnIndex) throws SQLException;

    protected abstract EID generateKey();

}
