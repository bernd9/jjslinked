package com.ejc.sql.processor;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
class EntityManagerModelFactory {
    private final EntityModel entityModel;


    static EntityManagerModel create(EntityModel entityModel) {
        return EntityManagerModel.builder()
                .entityModel(entityModel)
                .insertStatement(createInsertSql(entityModel))
                .build();
    }

    private static String createInsertSql(EntityModel entityModel) {
        List<String> idFieldNames = entityModel.getPrimaryKeyFields().stream()
                .map(EntityField::getColumnName)
                .sorted()
                .collect(Collectors.toList());

        return null;
    }


    private String createInsertSql(String tableName, List<String> idFieldNames, List<String> columnFieldNames) {
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

    private String createUpdateSql(String tableName, List<String> idFieldNames, List<String> columnFieldNames) {
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

    private String createDeleteSql(String tableName, List<String> idFieldNames) {
        StringBuilder sql = new StringBuilder().append("DELETE FROM `")
                .append(tableName)
                .append("` WHERE ");
        sql.append(idFieldNames.stream()
                .map(column -> String.format("`%d`= ?"))
                .collect(Collectors.joining(" AND ")));
        return sql.toString();
    }

    private List<String> pKColumnNames(EntityModel entityModel) {
        return entityModel.getPrimaryKeyFields().stream()
                .map(EntityField::getColumnName)
                .collect(Collectors.toList());
    }

}
