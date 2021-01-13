package com.ejc.sql;

public interface Repository<E> {
    
    void save(E entity);

    void delete(E entity);
}
