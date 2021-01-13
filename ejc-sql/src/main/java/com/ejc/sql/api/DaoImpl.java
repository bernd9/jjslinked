package com.ejc.sql.api;

import com.ejc.Init;
import com.ejc.Inject;
import com.ejc.api.context.ClassReference;
import com.ejc.sql.CrudRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DaoImpl<ID, T> implements CrudRepository<ID, T> {
    private final ORMapper mapper;
    private final List<String> idFieldNames = new ArrayList<>();
    private final List<String> columnFieldNames = new ArrayList<>();
    private final String tableName;
    private String insertSql;
    private String updateSql;
    private String deleteSql;

    @Inject
    private Connector connector;

    @Inject
    private SqlStatements sqlStatements;

    public DaoImpl(ClassReference entity, String tableName) {
        this.mapper = new ORMapper(entity);
        this.tableName = tableName;
    }

    @Init
    void init() {
        insertSql = sqlStatements.createInsertSql(tableName, idFieldNames, columnFieldNames);
        updateSql = sqlStatements.createUpdateSql(tableName, idFieldNames, columnFieldNames);
        deleteSql = sqlStatements.createDeleteSql(tableName, idFieldNames, columnFieldNames);
    }


    public void addIdField(String fieldName, Class<?> fieldType) {
        idFieldNames.add(fieldName);
        mapper.addField(fieldName, fieldType);
    }

    public void addColumnField(String fieldName, Class<?> fieldType) {
        columnFieldNames.add(fieldName);
        mapper.addField(fieldName, fieldType);
    }

    //@Override
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
            mapper.mapIntoStatement(entity, insert, fieldNames);
            return insert.executeUpdate();
        }
    }

    // @Override
    public int update(T entity) {
        try (Connection con = getConnection()) {
            return update(entity, con);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //@Override
    public T getById(Object... id) {
        // TODO
        return null;
    }

    private int update(T entity, Connection con) throws Exception {
        try (PreparedStatement update = createUpdateStatement(con)) {
            List<String> fieldNames = new ArrayList<>(idFieldNames);
            fieldNames.addAll(columnFieldNames);
            mapper.mapIntoStatement(entity, update, fieldNames);
            return update.executeUpdate();
        }
    }

    @Override
    public void save(T entity) {

    }

    @Override
    public void delete(T entity) {
        try (Connection con = getConnection()) {
            delete(entity, con);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int delete(T entity, Connection con) throws Exception {
        try (PreparedStatement delete = createDeleteStatement(con)) {
            mapper.mapIntoStatement(entity, delete, idFieldNames);
            return delete.executeUpdate();
        }
    }

    private Connection getConnection() {
        try {
            return connector.getConnection();
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

    @Override
    public T getById(ID id) {
        return null;
    }

    @Override
    public void deleteById(ID id) {

    }
}
