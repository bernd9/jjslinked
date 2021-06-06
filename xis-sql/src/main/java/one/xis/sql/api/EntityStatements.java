package one.xis.sql.api;

import one.xis.context.UsedInGeneratedCode;

public interface EntityStatements<E, EID> {

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    String getInsertSql();

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    String getSelectByIdSql();

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    String getUpdateSql();

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    String getDeleteSql();

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    String getSelectAllSql();

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    String getDeleteAllSql();

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    String getUpdateColumnValuesToNullByPkSql(String columnName);

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    String getSelectByColumnValueSql(String columnName);

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    String getCrossTableSelectSql(String crossTableName, String entityTableRef, String foreignTableRef);

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    void setInsertSqlParameters(JdbcStatement st, E entity);

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    void setUpdateSqlParameters(JdbcStatement st, E entity);

}
