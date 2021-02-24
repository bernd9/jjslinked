package one.xis.sql.api;

import one.xis.sql.Generated;
import one.xis.sql.JdbcException;
import one.xis.util.ChunkedList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
abstract class EntityTableAccessor<E, EID> extends JdbcExecutor {

    private static final String TEXT_NO_PK = "Entity has no primary key. Consider to set ";// TODO

    Optional<EntityProxy<E, EID>> getById(EID id) {
        try (PreparedStatement st = prepare(getSelectByPkSql())) {
            setId(st, 1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(toEntityProxy(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new JdbcException("failed to execute delete", e);
        }
    }

    abstract EntityProxy<E, EID> insert(E entity);

    abstract Set<EntityProxy<E, EID>> insert(Collection<E> entities);

    public List<EntityProxy<E, EID>> save(Collection<E> entities) {
        List<E> list = entities instanceof List ? ((List<E>) entities) : new ArrayList<>(entities);
        Set<EntityProxy<E, EID>> proxies = new HashSet<>();
        Set<E> nonProxies = new HashSet<>();
        List<EntityProxy<E, EID>> rv = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            E entity = list.get(0);
            if (entity instanceof EntityProxy) {
                proxies.add((EntityProxy<E, EID>) entity);
                rv.add((EntityProxy<E, EID>) entity);
            } else {
                nonProxies.add(entity);
                rv.add(toEntityProxy(entity));
            }
        }
        updateProxies(proxies);
        insert(nonProxies);
        return rv;
    }

    public void updateProxy(EntityProxy<E, EID> entityProxy) {
        if (entityProxy.isDirty()) {
            update(entityProxy.getEntity());
        }
    }

    public void updateProxies(Collection<EntityProxy<E, EID>> entityProxies) {
        update(entityProxies.stream()
                .filter(EntityProxy::isDirty)
                .map(EntityProxy::getEntity)
                .collect(Collectors.toSet()));
    }

    public EntityProxy<E, EID> update(E entity) {
        try (PreparedStatement st = prepare(getUpdateSql())) {
            setUpdateStatementParameters(st, entity);
            st.executeUpdate();
            return toEntityProxy(entity);
        } catch (SQLException e) {
            throw new JdbcException("failed to execute update", e);
        }
    }

    // TODO order may be important. Fix this here. Better list
    public Set<EntityProxy<E, EID>> update(Collection<E> entities) {
        ChunkedList<E> chunkedList = new ChunkedList<>(entities);
        Set<EntityProxy<E, EID>> proxies = new HashSet<>();
        while (!chunkedList.isEmpty()) {
            List<E> chunk = chunkedList.removeChunk(50);
            try (PreparedStatement st = prepare(getUpdateSql(chunk.size()))) {
                Iterator<E> chunkIterator = chunk.iterator();
                while (chunkIterator.hasNext()) {
                    E entity = chunkIterator.next();
                    setInsertStatementParameters(st, entity);
                    st.addBatch();
                    if (entity instanceof EntityProxy) {
                        throw new IllegalStateException();
                    }
                    proxies.add(toEntityProxy(entity));
                }
                st.executeBatch();
            } catch (SQLException e) {
                throw new JdbcException("failed to execute update", e);
            }
        }
        return proxies;
    }

    public boolean deleteById(E entity) {
        return delete(getId(entity));
    }

    public boolean delete(EID id) {
        try (PreparedStatement st = prepare(getDeleteSql())) {
            setId(st, 1, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new JdbcException("failed to execute delete", e);
        }
    }

    public void delete(Collection<E> entities) {
        ChunkedList<E> chunkedList = new ChunkedList<>(entities);
        while (!chunkedList.isEmpty()) {
            List<E> chunk = chunkedList.removeChunk(50);
            try (PreparedStatement st = prepare(getDeleteSql(chunk.size()))) {
                Iterator<E> chunkIterator = chunk.iterator();
                while (chunkIterator.hasNext()) {
                    E entity = chunkIterator.next();
                    setInsertStatementParameters(st, entity);
                    st.addBatch();
                }
                st.executeBatch();
            } catch (SQLException e) {
                throw new JdbcException("failed to execute delete", e);
            }
        }
    }

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
    private Set<EntityProxy<E, EID>> insertWithDbmsGeneratedKeys(Collection<E> entities) throws SQLException {
        Set<EntityProxy<E, EID>> proxies = new HashSet<>();
        ChunkedList<E> chunkedList = new ChunkedList<>(entities);
        while (!chunkedList.isEmpty()) {
            List<E> chunk = chunkedList.removeChunk(50);
            try (PreparedStatement st = prepare(getInsertSql(chunk.size()))) {
                Iterator<E> chunkIterator = chunk.iterator();
                while (chunkIterator.hasNext()) {
                    E entity = chunkIterator.next();
                    setInsertStatementParameters(st, entity);
                    st.addBatch();
                }
                st.executeBatch();
                ResultSet keys = st.getGeneratedKeys();
                chunkIterator = chunk.iterator();
                while (chunkIterator.hasNext()) {
                    if (!keys.next()) {
                        throw new JdbcException("number of keys does not match");
                    }
                    EID id = getId(keys, 1);
                    if (id == null) {
                        throw new JdbcException(""); // TODO
                    }
                    E entity = chunkIterator.next();
                    setId(entity, id);
                    proxies.add(toEntityProxy(entity));
                }
            }
        }
        return proxies;
    }

    private Set<EntityProxy<E, EID>> insertWithManuallyPlacedKeys(Collection<E> entities) throws SQLException {
        Set<EntityProxy<E, EID>> proxies = new HashSet<>();
        ChunkedList<E> chunkedList = new ChunkedList<>(entities);
        while (!chunkedList.isEmpty()) {
            List<E> chunk = chunkedList.removeChunk(50);
            try (PreparedStatement st = prepare(getInsertSql(chunk.size()))) {
                Iterator<E> chunkIterator = chunk.iterator();
                while (chunkIterator.hasNext()) {
                    E entity = chunkIterator.next();
                    EID id = getId(entity);
                    if (id == null) {
                        throw new JdbcException(""); // TODO
                    }
                    setInsertStatementParameters(st, entity);
                    st.addBatch();
                    proxies.add(toEntityProxy(entity));
                }
                st.executeBatch();
            }
        }
        return proxies;
    }

    protected Set<EntityProxy<E, EID>> insertWithApiGeneratedKeys(Collection<E> entities) throws SQLException {
        Set<EntityProxy<E, EID>> proxies = new HashSet<>();
        ChunkedList<E> chunkedList = new ChunkedList<>(entities);
        while (!chunkedList.isEmpty()) {
            List<E> chunk = chunkedList.removeChunk(50);
            try (PreparedStatement st = prepare(getInsertSql(chunk.size()))) {
                Iterator<E> chunkIterator = chunk.iterator();
                while (chunkIterator.hasNext()) {
                    E entity = chunkIterator.next();
                    EID id = generateKey();
                    setId(entity, id);
                    setInsertStatementParameters(st, entity);
                    st.addBatch();
                    proxies.add(toEntityProxy(entity));
                }
            }
        }
        return proxies;
    }

    abstract EntityProxy<E, EID> toEntityProxy(E entity);

    abstract EntityProxy<E, EID> toEntityProxy(ResultSet rs);

    abstract String getInsertSql();

    abstract String getInsertSql(int elementCount);

    abstract void setInsertStatementParameters(PreparedStatement st, E entity);

    abstract String getUpdateSql();

    abstract String getUpdateSql(int elementCount);

    abstract void setUpdateStatementParameters(PreparedStatement st, E entity);

    abstract String getDeleteSql();

    abstract String getDeleteSql(int elementCount);

    abstract String getSelectByPkSql();

    abstract void setId(E entity, EID id);

    abstract void setId(PreparedStatement st, int index, EID id);

    abstract EID getId(E entity);

    abstract EID getId(ResultSet rs, int index);

    abstract Generated getPrimaryKeySource();

    abstract EID generateKey();

}
