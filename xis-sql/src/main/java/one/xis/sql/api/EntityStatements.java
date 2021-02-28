package one.xis.sql.api;

public interface EntityStatements<E, EID> {

    String getInsertSql();

    String getSelectByIdSql();

    String getUpdateSql();

    String getDeleteSql();

    String getSelectAllSql();

    String getDeleteAllSql();

    void setInsertSqlParameters(PreparedEntityStatement st, E entity);

    void setUpdateSqlParameters(PreparedEntityStatement st, E entity);


}
