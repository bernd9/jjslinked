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

    @SuppressWarnings({"unchecked", "unused"})
    default <FID> FID pk(Object entity, Class<FID> pkType) {
        if (entity == null) return null;
        return ((EntityProxy<?,FID>) entity).pk();
    }


}
