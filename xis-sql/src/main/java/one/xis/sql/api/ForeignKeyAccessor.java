package one.xis.sql.api;

import one.xis.sql.JdbcException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

abstract class ForeignKeyAccessor<EID, FID> extends JdbcExecutor {

    /**
     * Intended for delete cascade by api
     *
     * @param key
     */
    void deleteAllReferencedFieldEntities(EID key) {
        try (PreparedStatement st = prepare(getDeleteAllReferencesSql())) {
            setEntityKey(st, 1, key);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new JdbcException("failed to close statement");
        }
    }


    /**
     * Intended for on delete to null by api
     *
     * @param key
     */
    void updateAllReferencesToNull(EID key) {
        try (PreparedStatement st = prepare(getUpdateAllReferencesToNullSql())) {
            setEntityKey(st, 1, key);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new JdbcException("failed to close statement");
        }

    }

    protected abstract String getDeleteAllReferencesSql();

    protected abstract String getUpdateAllReferencesToNullSql();


    void deleteAllChildNodes(EID parentId) {

    }

    void setFkToNullForAllChildNodes(EID parentId) {

    }

    public void deleteWhereNotIn(Stream<FID> retainFieldIds) {
    }

    public void setFkToNullExcept(Stream<FID> retainFieldIds) {

    }

    // TODO may be create a generated base class or entity-attributes for that
    protected abstract void setEntityKey(PreparedStatement st, int index, EID key) throws SQLException;

    protected abstract void setFieldKey(PreparedStatement st, int index, FID key) throws SQLException;

    protected abstract FID getFieldKey(ResultSet rs) throws SQLException;
}
