package one.xis.sql.api;

import com.ejc.api.context.UsedInGeneratedCode;
import one.xis.sql.GenerationStrategy;
import one.xis.sql.JdbcException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public abstract class EntityTableAccessor<E, EID, P extends EntityProxy<E,EID>> extends JdbcExecutor {

    private static final String TEXT_NO_PK = "Entity has no primary key. Consider to set ";// TODO

    Optional<E> getById(EID id) {
        try (PreparedEntityStatement st = prepare(getSelectByPkSql())) {
            setId(st, 1, id);
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
            throw new JdbcException("failed to execute select all", e);
        }
    }

    protected abstract void insert(P entityProxy);

    protected  abstract void insert(Collection<P> entityProxies);

    public Collection<P> save(Collection<E> entities) {
        if (entities instanceof EntityCollection) {
            if (!((EntityCollection<?>) entities).isDirty()) {
                return (Collection<P>) entities;
            }
        }
        Collection<P> rv;
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

    private void save(Collection<E> entities, Collection<P> saved) {
        List<P> proxiesForUpdate = new ArrayList<>();
        List<P> proxiesForInsert = new ArrayList<>();
        Iterator<E> entityIterator = entities.iterator();
        while (entityIterator.hasNext()) {
            E entity = entityIterator.next();
            P entityProxy;
            if (entity instanceof EntityProxy) {
                entityProxy = (P) entity;
                if (entityProxy.dirty()) {
                    if (entityProxy.stored()) {
                        proxiesForUpdate.add(entityProxy);
                    } else {
                        proxiesForInsert.add(entityProxy);
                    }
                }
                saved.add((P) entity);
            } else {
                entityProxy = toEntityProxy(entity, true);
                proxiesForInsert.add(entityProxy);
                saved.add(entityProxy);
            }
        }
        updateProxies(proxiesForUpdate);
        insert(proxiesForInsert);
    }

    public void updateProxies(List<P> entityProxies) {
        // TODO : We reinsert deleted entries ?
       insertWithManuallyPlacedKeys(update(entityProxies));
    }


    private Collection<P> update(List<P> entityProxies) {
        Set<P> failedEntities = new HashSet<>();
        Iterator<P> entityProxyIterator = entityProxies.iterator();
        try (PreparedStatement st = prepare(getUpdateSql())) {
            while (entityProxyIterator.hasNext()) {
                P proxy  = entityProxyIterator.next();
                if (!proxy.stored() || proxy.pk() == null) {
                    throw new IllegalStateException();
                }
                E entity = proxy.entity();
                st.clearParameters();
                setUpdateStatementParameters(st, entity);
                st.addBatch();
                proxy.doSetClean();
            }
            int[] result = st.executeBatch();
            for (int index = 0; index < result.length; index++) {
                if (result[index] == 0) {
                    failedEntities.add(entityProxies.get(index));
                }
            }
            return failedEntities;
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
    protected void insertWithDbmsGeneratedKey(P entityProxy) {
        if (entityProxy.stored()) {
            throw new IllegalStateException();
        }
        try (PreparedStatement st = prepare(getInsertSql(), Statement.RETURN_GENERATED_KEYS)) {
            setInsertStatementParameters(st, entityProxy.entity());
            st.executeUpdate();
            ResultSet keys = st.getGeneratedKeys();
            if (!keys.next()) {
                throw new JdbcException("no pk was generated for " + entityProxy);
            }
            EID id = getId(keys, 1);
            entityProxy.pk(id);
            entityProxy.doSetStored();
            entityProxy.doSetClean();
        } catch (SQLException e) {
            throw new JdbcException("failed to close statement", e);
        }
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void insertWithManuallyPlacedKey(P entityProxy) {
        if (entityProxy.stored()) {
            throw new IllegalStateException();
        }
        try (PreparedStatement st = prepare(getInsertSql())) {
            setInsertStatementParameters(st, entityProxy.entity());
            st.executeUpdate();
            if (entityProxy.pk() == null) {
                throw new JdbcException(entityProxy.entity() + ": " + TEXT_NO_PK);
            }
            entityProxy.doSetStored();
            entityProxy.doSetClean();
        } catch (SQLException e) {
            throw new JdbcException("failed to close statement", e);
        }
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void insertWithApiGeneratedKey(P entityProxy) {
        if (entityProxy.stored()) {
            throw new IllegalStateException();
        }
        try (PreparedStatement st = prepare(getInsertSql(), Statement.RETURN_GENERATED_KEYS)) {
            entityProxy.pk(generateKey());
            setInsertStatementParameters(st, entityProxy.entity());
            st.executeUpdate();
            entityProxy.doSetStored();
            entityProxy.doSetClean();
            ResultSet keys = st.getGeneratedKeys();
            if (!keys.next()) {
                throw new JdbcException("no pk was generated for " + entityProxy.entity());
            }
        } catch (SQLException e) {
            throw new JdbcException("failed to close statement", e);
        }
    }

    // TODO order may be important. Fix this here. Better list
    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void insertWithDbmsGeneratedKeys(Collection<P> entityProxies) {
        Iterator<P> entityIterator = entityProxies.iterator();
        try (PreparedStatement st = prepare(getInsertSql())) {
            while (entityIterator.hasNext()) {
                P entityProxy = entityIterator.next();
                if (entityProxy.stored()) {
                    throw new IllegalStateException();
                }
                st.clearParameters();
                setInsertStatementParameters(st, entityProxy.entity());
                st.addBatch();
                entityProxy.doSetStored();
                entityProxy.doSetClean();
            }
            st.executeBatch();
            ResultSet keys = st.getGeneratedKeys();
            entityIterator = entityProxies.iterator();
            while (entityIterator.hasNext()) {
                if (!keys.next()) {
                    throw new JdbcException("number of keys does not match");
                }
                EID id = getId(keys, 1);
                if (id == null) {
                    throw new JdbcException(""); // TODO
                }
                entityIterator.next().pk(id);
            }

        } catch (SQLException e) {
            throw new JdbcException("failed to execute bulk-insert", e);
        }
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void insertWithManuallyPlacedKeys(Collection<P> entityProxies) {
        Iterator<P> entityIterator = entityProxies.iterator();
        try (PreparedStatement st = prepare(getInsertSql())) {
            while (entityIterator.hasNext()) {
                P entityProxy = entityIterator.next();
                if (entityProxy.pk() == null) {
                    throw new JdbcException(""); // TODO
                }
                if (entityProxy.stored()) {
                    throw new IllegalStateException();
                }
                st.clearParameters();
                setInsertStatementParameters(st, entityProxy.entity());
                st.addBatch();
                entityProxy.doSetStored();
                entityProxy.doSetClean();
            }
            st.executeBatch();
        } catch (SQLException e) {
            throw new JdbcException("failed to execute bulk-insert", e);
        }
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void insertWithApiGeneratedKeys(Collection<P> entityProxies) {
        Iterator<P> entityIterator = entityProxies.iterator();
        try (PreparedStatement st = prepare(getInsertSql())) {
            while (entityIterator.hasNext()) {
                P entityProxy = entityIterator.next();
                if (entityProxy.stored()) {
                    throw new IllegalStateException();
                }
                entityProxy.pk(generateKey());
                st.clearParameters();
                setInsertStatementParameters(st, entityProxy.entity());
                st.addBatch();
                entityProxy.doSetStored();
                entityProxy.doSetClean();
            }
            st.executeBatch();
        } catch (SQLException e) {
            throw new JdbcException("failed to execute bulk-insert", e);
        }
    }


    private P toEntityProxy(ResultSet rs) throws SQLException {
        return toEntityProxy(toEntity(rs), true);
    }


    protected abstract P toEntityProxy(E entity, boolean stored);

    protected abstract E toEntity(ResultSet rs) throws SQLException;

    protected abstract String getInsertSql();

    protected abstract void setInsertStatementParameters(PreparedStatement st, E entity);

    protected abstract String getUpdateSql();

    protected abstract void setUpdateStatementParameters(PreparedStatement st, E entity);

    protected abstract String getDeleteSql();

    protected abstract String getSelectByPkSql();

    protected abstract String getSelectAllSql();

    // TODO replace with concrete id-type
    protected abstract void setId(PreparedStatement st, int index, EID id);

    protected abstract EID getId(E entity);

    protected abstract EID getId(ResultSet rs, int index) throws SQLException;

    protected abstract GenerationStrategy getPrimaryKeySource();

    protected abstract EID generateKey();

}
