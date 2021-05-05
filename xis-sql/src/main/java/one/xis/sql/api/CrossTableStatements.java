package one.xis.sql.api;

import lombok.Getter;

@Getter
public class CrossTableStatements<F, FID> {
    private final String deleteReferencesOfEntitySql;
    private final String insertReferencesOfEntitySql;
    private final String crossTableName;
    private final String entityKeyColumnName;
    private final String fieldKeyColumnName;
    private final EntityStatements<F, FID> fieldEntityStatements;

    public CrossTableStatements(String crossTableName, String entityKeyColumnName, String fieldKeyColumnName, EntityStatements<F, FID> fieldEntityStatements) {
        this.crossTableName = crossTableName;
        this.entityKeyColumnName = entityKeyColumnName;
        this.fieldKeyColumnName = fieldKeyColumnName;
        this.fieldEntityStatements = fieldEntityStatements;
        this.deleteReferencesOfEntitySql = String.format("DELETE FROM %s WHERE %s=? AND %s=?", crossTableName, entityKeyColumnName, fieldKeyColumnName);
        this.insertReferencesOfEntitySql = String.format("INSERT INTO %s (%s,%s) VALUES (?,?)", crossTableName, entityKeyColumnName, fieldKeyColumnName);
    }

    String getJoinSql() {
        return fieldEntityStatements.getCrossTableSelectSql(crossTableName, fieldKeyColumnName, entityKeyColumnName); // Swapped names. Here, field=entity
    }


}
