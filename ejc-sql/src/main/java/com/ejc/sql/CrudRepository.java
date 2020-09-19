package com.ejc.sql;

public interface CrudRepository<T> {

    int insert(T entity);

    int update(T entity);

    void getById(Object... id);

    int delete(T entity);

}
