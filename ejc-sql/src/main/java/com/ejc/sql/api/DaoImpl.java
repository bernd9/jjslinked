package com.ejc.sql.api;

import com.ejc.ApplicationContext;
import com.ejc.api.context.ClassReference;
import com.ejc.sql.Connector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DaoImpl<T> {
    private final EntityMapper entityMapper;
    private final List<String> idFieldNames = new ArrayList<>();
    private final List<String> columnFieldNames = new ArrayList<>();
    private final String tableName;
    private String insertSql;

    public DaoImpl(ClassReference entity, String tableName) {
        this.entityMapper = new EntityMapper(entity);
        this.tableName = tableName;
    }

    public void addIdField(String fieldName, Class<?> fieldType) {
        idFieldNames.add(fieldName);
        entityMapper.addField(fieldName, fieldType);
    }

    public void addColumnField(String fieldName, Class<?> fieldType) {
        columnFieldNames.add(fieldName);
        entityMapper.addField(fieldName, fieldType);
    }

    public void init() {
        insertSql = createInsertSql();
    }


    public int insert(T entity) {
        try (Connection con = getConnection()) {
            return insert(entity, con);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int insert(T entity, Connection con) throws Exception {
        try (PreparedStatement insert = createInsertStatement(con)) {
            List<String> fieldNames = new ArrayList<>(idFieldNames);
            fieldNames.addAll(columnFieldNames);
            entityMapper.mapIntoStatement(entity, insert, fieldNames);
            return insert.executeUpdate();
        }
    }


    private Connection getConnection() {
        return ApplicationContext.getInstance().getBean(Connector.class).getConnection();
    }

    private String createInsertSql() {
        StringBuilder sql = new StringBuilder().append("INSERT INTO `")
                .append(tableName)
                .append(" (");
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

    private PreparedStatement createInsertStatement(Connection con) throws SQLException {
        return con.prepareStatement(insertSql);
    }

}
