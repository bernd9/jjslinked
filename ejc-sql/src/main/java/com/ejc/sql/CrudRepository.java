package com.ejc.sql;

public interface CrudRepository<T> {

    int insert(T entity);

    int update(T entity);

    T getById(Object... id);

    int delete(T entity);

}
