package one.xis.sql.api;

import one.xis.sql.GenerationStrategy;
import one.xis.sql.JdbcException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

abstract class EntityTableAccessor<E, EID> extends JdbcExecutor {

    private static final String TEXT_NO_PK = "Entity has no primary key. Consider to set ";// TODO

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    List<E> findAll() {
        EntityArrayList<E> list = new EntityArrayList<>();
        findAll(list);
        return list;
    }

    @SuppressWarnings("unchecked")
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

    protected abstract E insert(EntityProxy<E, EID> entityProxy);

    protected abstract Set<E> insert(Collection<EntityProxy<E, EID>> entityProxies);

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unused")
    private EntityProxy<E, EID> insertWithDbmsGeneratedKey(E entity) {
        try (PreparedStatement st = prepare(getInsertSql(), Statement.RETURN_GENERATED_KEYS)) {
            setInsertStatementParameters(st, entity);
            st.executeUpdate();
            ResultSet keys = st.getGeneratedKeys();
            if (!keys.next()) {
                throw new JdbcException("no pk was generated for " + entity);
            }
            EID id = getId(keys, 1);
            setId(entity, id);
            return toEntityProxy(entity);
        } catch (SQLException e) {
            throw new JdbcException("failed to close statement", e);
        }
    }

    @SuppressWarnings("unused")
    private EntityProxy<E, EID> insertWithManuallyPlacedKey(E entity) {
        try (PreparedStatement st = prepare(getInsertSql())) {
            setInsertStatementParameters(st, entity);
            st.executeUpdate();
            EID id = getId(entity);
            if (id == null) {
                throw new JdbcException(entity + ": " + TEXT_NO_PK);
            }
            return toEntityProxy(entity);
        } catch (SQLException e) {
            throw new JdbcException("failed to close statement", e);
        }
    }

    @SuppressWarnings("unused")
    private EntityProxy<E, EID> insertWithApiGeneratedKey(E entity) {
        try (PreparedStatement st = prepare(getInsertSql(), Statement.RETURN_GENERATED_KEYS)) {
            EID id = generateKey();
            setId(entity, id);
            setInsertStatementParameters(st, entity);
            st.executeUpdate();
            ResultSet keys = st.getGeneratedKeys();
            if (!keys.next()) {
                throw new JdbcException("no pk was generated for " + entity);
            }
            return toEntityProxy(entity);
        } catch (SQLException e) {
            throw new JdbcException("failed to close statement", e);
        }
    }

    // TODO order may be important. Fix this here. Better list
    @SuppressWarnings("unused")
    private Set<EntityProxy<E, EID>> insertWithDbmsGeneratedKeys(Collection<EntityProxy<E, EID>> entities) throws SQLException {
        Iterator<EntityProxy<E, EID>> entityIterator = entities.iterator();
        Set<EntityProxy<E, EID>> proxies = new HashSet<>();
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
                EntityProxy<E, EID> entityProxy = entityIterator.next();
                E entity = entityProxy.getEntity();
                setId(entity, id);
                proxies.add(toEntityProxy(entity));
            }

        }
        return proxies;
    }

    @SuppressWarnings("unused")
    private Set<EntityProxy<E, EID>> insertWithManuallyPlacedKeys(Collection<EntityProxy<E, EID>> entities) throws SQLException {
        Iterator<EntityProxy<E, EID>> entityIterator = entities.iterator();
        Set<EntityProxy<E, EID>> proxies = new HashSet<>();
        try (PreparedStatement st = prepare(getInsertSql())) {
            while (entityIterator.hasNext()) {
                EntityProxy<E, EID> entityProxy = entityIterator.next();
                st.clearParameters();
                setInsertStatementParameters(st, entityProxy.getEntity());
                st.addBatch();
                proxies.add(entityProxy);

            }
            st.executeBatch();
        }
        return proxies;
    }

    @SuppressWarnings("unused")
    protected Set<EntityProxy<E, EID>> insertWithApiGeneratedKeys(Collection<EntityProxy<E, EID>> entities) throws SQLException {
        Iterator<EntityProxy<E, EID>> entityIterator = entities.iterator();
        Set<EntityProxy<E, EID>> proxies = new HashSet<>();
        try (PreparedStatement st = prepare(getInsertSql())) {
            while (entityIterator.hasNext()) {
                EntityProxy<E, EID> entityProxy = entityIterator.next();
                entityProxy.setPkPrivileged(generateKey());
                st.clearParameters();
                setInsertStatementParameters(st, entityProxy.getEntity());
                st.addBatch();
                proxies.add(entityProxy);

            }
            st.executeBatch();
        }
        return proxies;
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
