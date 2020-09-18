package com.ejc.sql;

public interface CrudRepository<T,ID> {

    default void save(T entity) {

    }

    void insert(T entity);
    void update(T entity);
    void getById(ID id);
    void delete(T entity);
}
