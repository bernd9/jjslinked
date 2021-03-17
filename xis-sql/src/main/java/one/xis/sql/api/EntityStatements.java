package one.xis.sql.api;

import com.ejc.api.context.UsedInGeneratedCode;

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
    void setInsertSqlParameters(PreparedEntityStatement st, E entity);

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    void setUpdateSqlParameters(PreparedEntityStatement st, E entity);

}
