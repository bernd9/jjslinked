package com.ejc.sql.api;

import com.ejc.ApplicationContext;
import com.ejc.api.context.ClassReference;
import com.ejc.sql.Connector;
import com.ejc.sql.CrudRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DaoImpl<T> implements CrudRepository<T> {
    private final EntityMapper entityMapper;
    private final List<String> idFieldNames = new ArrayList<>();
    private final List<String> columnFieldNames = new ArrayList<>();
    private final String tableName;
    private String insertSql;
    private String updateSql;
    private String deleteSql;

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
        updateSql = createUpdateSql();
        deleteSql = createDeleteSql();
    }


    @Override
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

    @Override
    public int update(T entity) {
        try (Connection con = getConnection()) {
            return update(entity, con);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getById(Object... id) {
        // TODO
    }

    private int update(T entity, Connection con) throws Exception {
        try (PreparedStatement update = createUpdateStatement(con)) {
            List<String> fieldNames = new ArrayList<>(idFieldNames);
            fieldNames.addAll(columnFieldNames);
            entityMapper.mapIntoStatement(entity, update, fieldNames);
            return update.executeUpdate();
        }
    }

    @Override
    public int delete(T entity) {
        try (Connection con = getConnection()) {
            return delete(entity, con);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int delete(T entity, Connection con) throws Exception {
        try (PreparedStatement delete = createDeleteStatement(con)) {
            entityMapper.mapIntoStatement(entity, delete, idFieldNames);
            return delete.executeUpdate();
        }
    }

    private Connection getConnection() {
        try {
            return ApplicationContext.getInstance().getBean(Connector.class).getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement createInsertStatement(Connection con) throws SQLException {
        return con.prepareStatement(insertSql);
    }

    private PreparedStatement createUpdateStatement(Connection con) throws SQLException {
        return con.prepareStatement(updateSql);
    }


    private PreparedStatement createDeleteStatement(Connection con) throws SQLException {
        return con.prepareStatement(deleteSql);
    }


    private String createInsertSql() {
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

    private String createUpdateSql() {
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

    private String createDeleteSql() {
        StringBuilder sql = new StringBuilder().append("DELETE FROM `")
                .append(tableName)
                .append("` WHERE ");
        sql.append(idFieldNames.stream()
                .map(column -> String.format("`%d`= ?"))
                .collect(Collectors.joining(" AND ")));
        return sql.toString();
    }


}
