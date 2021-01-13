package com.ejc.sql;

public interface CrudRepository<ID, E> extends Repository<E> {


    E getById(ID id);

    void deleteById(ID id);

}
