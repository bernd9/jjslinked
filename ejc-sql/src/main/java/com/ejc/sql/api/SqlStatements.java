package com.ejc.sql.api;

import com.ejc.Singleton;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
class SqlStatements {

    String createInsertSql(String tableName, List<String> idFieldNames, List<String> columnFieldNames) {
        StringBuilder sql = new StringBuilder().append("INSERT INTO `")
                .append(tableName)
                .append("` (");
        sql.append(Stream.concat(idFieldNames.stream(), columnFieldNames.stream())
                .map(column -> String.format("`%d`", column))
                .collect(Collectors.joining(", ")));
        sql.append(") values (");
        sql.append(Stream.concat(idFieldNames.stream(), columnFieldNames.stream())
                .map(column -> "?")
                .collect(Collectors.joining(", ")));
        sql.append(")");
        return sql.toString();
    }

    String createUpdateSql(String tableName, List<String> idFieldNames, List<String> columnFieldNames) {
        StringBuilder sql = new StringBuilder().append("UPDATE `")
                .append(tableName)
                .append("` ");
        sql.append(columnFieldNames.stream()
                .map(column -> String.format("SET `%d`=?", column))
                .collect(Collectors.joining(", ")));
        sql.append(" WHERE ");
        sql.append(idFieldNames.stream()
                .map(column -> String.format("`%d`= ?"))
                .collect(Collectors.joining(" AND ")));
        return sql.toString();
    }

    String createDeleteSql(String tableName, List<String> idFieldNames, List<String> columnFieldNames) {
        StringBuilder sql = new StringBuilder().append("DELETE FROM `")
                .append(tableName)
                .append("` WHERE ");
        sql.append(idFieldNames.stream()
                .map(column -> String.format("`%d`= ?"))
                .collect(Collectors.joining(" AND ")));
        return sql.toString();
    }
}
