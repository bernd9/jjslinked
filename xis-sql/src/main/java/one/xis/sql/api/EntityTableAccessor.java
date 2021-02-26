package one.xis.sql.api;

import com.ejc.api.context.UsedInGeneratedCode;
import one.xis.sql.GenerationStrategy;
import one.xis.sql.JdbcException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

public abstract class EntityTableAccessor<E, EID> extends JdbcExecutor {

    private static final String TEXT_NO_PK = "Entity has no primary key. Consider to set ";// TODO

    Optional<E> getById(EID id) {
        try (PreparedStatement st = prepare(getSelectByPkSql())) {
            setId(st, 1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return Optional.of((E) toEntityProxy(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new JdbcException("failed to execute delete", e);
        }
    }

    List<E> lazyLoadAll() {
        return new LazyLoadingEntityArrayList<>(() -> {
            final List<E> list = new ArrayList<>();
            findAll(list);
            return list;
        });
    }

    List<E> findAll() {
        EntityArrayList<E> list = new EntityArrayList<>();
        findAll(list);
        return list;
    }

    private void findAll(Collection<E> coll) {
        try (PreparedStatement st = prepare(getSelectAllSql())) {
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    coll.add((E) toEntityProxy(rs));
                }
            }
        } catch (SQLException e) {
            throw new JdbcException("failed to execute delete", e);
        }
    }

    protected abstract void insert(EntityProxy<E, EID> entityProxy);

    protected abstract void insert(Collection<EntityProxy<E, EID>> entityProxies);

    public Collection<EntityProxy<E, EID>> save(Collection<E> entities) {
        if (entities instanceof EntityCollection) {
            if (!((EntityCollection<?>) entities).isDirty()) {
                return (Collection<EntityProxy<E, EID>>) entities;
            }
        }
        Collection<EntityProxy<E, EID>> rv;
        if (entities instanceof ArrayList) {
            rv = new EntityArrayList<>();
            save(entities, rv);
        } else if (entities instanceof List) {
            rv = new EntityArrayList<>();
            save(entities, rv);
        } else {
            throw new IllegalArgumentException();
        }
        return rv;
    }

    private void save(Collection<E> entities, Collection<EntityProxy<E, EID>> saved) {
        Set<EntityProxy<E, EID>> proxiesForUpdate = new HashSet<>();
        List<EntityProxy<E, EID>> proxiesForInsert = new ArrayList<>();
        Iterator<E> entityIterator = entities.iterator();
        while (entityIterator.hasNext()) {
            E entity = entityIterator.next();
            EntityProxy<E, EID> entityProxy;
            if (entity instanceof EntityProxy) {
                entityProxy = (EntityProxy<E, EID>) entity;
                if (entityProxy.isDirty()) {
                    proxiesForUpdate.add(entityProxy);
                    entityProxy.setClean();
                }
                saved.add((EntityProxy<E, EID>) entity);
            } else {
                entityProxy = toEntityProxy(entity);
                proxiesForInsert.add(entityProxy);
                saved.add(entityProxy);
            }
        }
        updateProxies(proxiesForUpdate);
        insert(proxiesForInsert);
    }

    public void updateProxies(Collection<EntityProxy<E, EID>> entityProxies) {
        update(entityProxies.stream()
                .filter(EntityProxy::isDirty)
                .map(EntityProxy::getEntity)
                .collect(Collectors.toSet()));
    }


    private void update(Collection<E> entities) {
        Iterator<E> entityIterator = entities.iterator();
        try (PreparedStatement st = prepare(getUpdateSql())) {
            while (entityIterator.hasNext()) {
                E entity = entityIterator.next();
                st.clearParameters();
                setUpdateStatementParameters(st, entity);
                st.addBatch();
                if (entity instanceof EntityProxy) {
                    throw new IllegalStateException();
                }
            }
            st.executeBatch();
        } catch (SQLException e) {
            throw new JdbcException("failed to execute update", e);
        }
    }

    public boolean delete(E entity) {
        return deleteById(getId(entity));
    }

    public boolean deleteById(EID id) {
        try (PreparedStatement st = prepare(getDeleteSql())) {
            setId(st, 1, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new JdbcException("failed to execute delete", e);
        }
    }

    public void delete(Collection<E> entities) {
        Iterator<E> entityIterator = entities.iterator();
        try (PreparedStatement st = prepare(getDeleteSql())) {
            while (entityIterator.hasNext()) {
                st.clearParameters();
                E entity = entityIterator.next();
                setId(st, 1, getId(entity));
                st.addBatch();
            }
            st.executeBatch();
        } catch (SQLException e) {
            throw new JdbcException("failed to execute delete", e);
        }
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void insertWithDbmsGeneratedKey(EntityProxy<E, EID> entityProxy) {
        try (PreparedStatement st = prepare(getInsertSql(), Statement.RETURN_GENERATED_KEYS)) {
            setInsertStatementParameters(st, entityProxy.getEntity());
            st.executeUpdate();
            ResultSet keys = st.getGeneratedKeys();
            if (!keys.next()) {
                throw new JdbcException("no pk was generated for " + entityProxy);
            }
            EID id = getId(keys, 1);
            entityProxy.setPkPrivileged(id);
        } catch (SQLException e) {
            throw new JdbcException("failed to close statement", e);
        }
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void insertWithManuallyPlacedKey(EntityProxy<E, EID> entityProxy) {
        try (PreparedStatement st = prepare(getInsertSql())) {
            setInsertStatementParameters(st, entityProxy.getEntity());
            st.executeUpdate();
            if (entityProxy.pk() == null) {
                throw new JdbcException(entityProxy.getEntity() + ": " + TEXT_NO_PK);
            }
        } catch (SQLException e) {
            throw new JdbcException("failed to close statement", e);
        }
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void insertWithApiGeneratedKey(EntityProxy<E, EID> entityProxy) {
        try (PreparedStatement st = prepare(getInsertSql(), Statement.RETURN_GENERATED_KEYS)) {
            entityProxy.setPkPrivileged(generateKey());
            setInsertStatementParameters(st, entityProxy.getEntity());
            st.executeUpdate();
            ResultSet keys = st.getGeneratedKeys();
            if (!keys.next()) {
                throw new JdbcException("no pk was generated for " + entityProxy.getEntity());
            }
        } catch (SQLException e) {
            throw new JdbcException("failed to close statement", e);
        }
    }

    // TODO order may be important. Fix this here. Better list
    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void insertWithDbmsGeneratedKeys(Collection<EntityProxy<E, EID>> entities) {
        Iterator<EntityProxy<E, EID>> entityIterator = entities.iterator();
        try (PreparedStatement st = prepare(getInsertSql())) {
            while (entityIterator.hasNext()) {
                EntityProxy<E, EID> entityProxy = entityIterator.next();
                st.clearParameters();
                setInsertStatementParameters(st, entityProxy.getEntity());
                st.addBatch();
            }
            st.executeBatch();
            ResultSet keys = st.getGeneratedKeys();
            entityIterator = entities.iterator();
            while (entityIterator.hasNext()) {
                if (!keys.next()) {
                    throw new JdbcException("number of keys does not match");
                }
                EID id = getId(keys, 1);
                if (id == null) {
                    throw new JdbcException(""); // TODO
                }
                entityIterator.next().setPkPrivileged(id);
            }

        } catch (SQLException e) {
            throw new JdbcException("failed to execute bulk-insert", e);
        }
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void insertWithManuallyPlacedKeys(Collection<EntityProxy<E, EID>> entities) {
        Iterator<EntityProxy<E, EID>> entityIterator = entities.iterator();
        try (PreparedStatement st = prepare(getInsertSql())) {
            while (entityIterator.hasNext()) {
                EntityProxy<E, EID> entityProxy = entityIterator.next();
                if (entityProxy.pk() == null) {
                    throw new JdbcException(""); // TODO
                }
                st.clearParameters();
                setInsertStatementParameters(st, entityProxy.getEntity());
                st.addBatch();
            }
            st.executeBatch();
        } catch (SQLException e) {
            throw new JdbcException("failed to execute bulk-insert", e);
        }
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void insertWithApiGeneratedKeys(Collection<EntityProxy<E, EID>> entities) {
        Iterator<EntityProxy<E, EID>> entityIterator = entities.iterator();
        try (PreparedStatement st = prepare(getInsertSql())) {
            while (entityIterator.hasNext()) {
                EntityProxy<E, EID> entityProxy = entityIterator.next();
                entityProxy.setPkPrivileged(generateKey());
                st.clearParameters();
                setInsertStatementParameters(st, entityProxy.getEntity());
                st.addBatch();
            }
            st.executeBatch();
        } catch (SQLException e) {
            throw new JdbcException("failed to execute bulk-insert", e);
        }
    }

    protected abstract EntityProxy<E, EID> toEntityProxy(E entity);

    protected abstract EntityProxy<E, EID> toEntityProxy(ResultSet rs) throws SQLException;

    protected abstract String getInsertSql();

    protected abstract void setInsertStatementParameters(PreparedStatement st, E entity);

    protected abstract String getUpdateSql();

    protected abstract void setUpdateStatementParameters(PreparedStatement st, E entity);

    protected abstract String getDeleteSql();

    protected abstract String getSelectByPkSql();

    protected abstract String getSelectAllSql();

    protected abstract void setId(E entity, EID id);

    protected abstract void setId(PreparedStatement st, int index, EID id);

    protected abstract EID getId(E entity);

    protected abstract EID getId(ResultSet rs, int index) throws SQLException;

    protected abstract GenerationStrategy getPrimaryKeySource();

    protected abstract EID generateKey();

}
