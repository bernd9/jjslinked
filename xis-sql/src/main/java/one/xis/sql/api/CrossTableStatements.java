package one.xis.sql.api;

import lombok.Getter;

@Getter
public class CrossTableStatements {
    private final String deleteReferencesOfEntitySql;
    private final String insertReferencesOfEntitySql;

    CrossTableStatements(String crossTableName, String entityKeyColumnName, String fieldKeyColumnName) {
        this.deleteReferencesOfEntitySql = String.format("DELETE FROM %s WHERE %s=? AND %s=?", crossTableName, entityKeyColumnName, fieldKeyColumnName);
        this.insertReferencesOfEntitySql = String.format("INSERT INTO %s (%s,%s) VALUES (?,?)", crossTableName, entityKeyColumnName, fieldKeyColumnName);
    }


}
