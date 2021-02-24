package one.xis.sql.api;

import one.xis.sql.JdbcException;
import one.xis.sql.PrimaryKeySource;
import one.xis.util.ChunkedList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@SuppressWarnings("unused")
abstract class EntityTableAccessor<E, EID> extends JdbcExecutor {

    private static final String TEXT_NO_PK = "Entity has no primary key. Consider to set ";// TODO

    Optional<E> getById(EID id) {
        try (PreparedStatement st = prepare(getSelectByPkSql())) {
            setId(st, 1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(toEntity(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new JdbcException("failed to execute delete", e);
        }
    }


    abstract void insert(E entity);

    abstract void insert(Collection<E> entities);


    public boolean update(E entity) {
        try (PreparedStatement st = prepare(getUpdateSql())) {
            setUpdateStatementParameters(st, entity);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new JdbcException("failed to execute update", e);
        }
    }

    public void update(Collection<E> entities) {
        ChunkedList<E> chunkedList = new ChunkedList<>(entities);
        while (!chunkedList.isEmpty()) {
            List<E> chunk = chunkedList.removeChunk(50);
            try (PreparedStatement st = prepare(getUpdateSql(chunk.size()))) {
                Iterator<E> chunkIterator = chunk.iterator();
                while (chunkIterator.hasNext()) {
                    E entity = chunkIterator.next();
                    setInsertStatementParameters(st, entity);
                    st.addBatch();
                }
                st.executeBatch();
            } catch (SQLException e) {
                throw new JdbcException("failed to execute update", e);
            }
        }
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

    private EID insertWithDbmsGeneratedKey(E entity) {
        try (PreparedStatement st = prepare(getInsertSql(), Statement.RETURN_GENERATED_KEYS)) {
            setInsertStatementParameters(st, entity);
            st.executeUpdate();
            ResultSet keys = st.getGeneratedKeys();
            if (!keys.next()) {
                throw new JdbcException("no pk was generated for " + entity);
            }
            EID id = getId(keys, 1);
            setId(entity, id);
            return id;
        } catch (SQLException e) {
            throw new JdbcException("failed to close statement", e);
        }
    }

    private EID insertWithManuallyPlacedKey(E entity) {
        try (PreparedStatement st = prepare(getInsertSql())) {
            setInsertStatementParameters(st, entity);
            st.executeUpdate();
            EID id = getId(entity);
            if (id == null) {
                throw new JdbcException(entity + ": " + TEXT_NO_PK);
            }
            return id;
        } catch (SQLException e) {
            throw new JdbcException("failed to close statement", e);
        }
    }

    private EID insertWithApiGeneratedKey(E entity) {
        try (PreparedStatement st = prepare(getInsertSql(), Statement.RETURN_GENERATED_KEYS)) {
            EID id = generateKey();
            setId(entity, id);
            setInsertStatementParameters(st, entity);
            st.executeUpdate();
            ResultSet keys = st.getGeneratedKeys();
            if (!keys.next()) {
                throw new JdbcException("no pk was generated for " + entity);
            }
            return id;
        } catch (SQLException e) {
            throw new JdbcException("failed to close statement", e);
        }
    }


    private Set<EID> insertWithDbmsGeneratedKeys(Collection<E> entities) throws SQLException {
        Set<EID> ids = new HashSet<>();
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
                    setId(chunkIterator.next(), id);
                    ids.add(id);
                }
            }
        }
        return ids;
    }

    private Set<EID> insertWithManuallyPlacedKeys(Collection<E> entities) throws SQLException {
        Set<EID> ids = new HashSet<>();
        ChunkedList<E> chunkedList = new ChunkedList<>(entities);
        while (!chunkedList.isEmpty()) {
            List<E> chunk = chunkedList.removeChunk(50);
            try (PreparedStatement st = prepare(getInsertSql(chunk.size()))) {
                Iterator<E> chunkIterator = chunk.iterator();
                while (chunkIterator.hasNext()) {
                    E entity = chunkIterator.next();
                    setInsertStatementParameters(st, entity);
                    EID id = getId(entity);
                    ids.add(id);
                    st.addBatch();
                }
                st.executeBatch();
            }
        }
        return ids;
    }

    protected Set<EID> insertWithApiGeneratedKeys(Collection<E> entities) throws SQLException {
        Set<EID> ids = new HashSet<>();
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
                    ids.add(id);
                }
            }
        }
        return ids;
    }

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

    abstract PrimaryKeySource getPrimaryKeySource();

    abstract EID generateKey();

    abstract E toEntity(ResultSet rs);

}
