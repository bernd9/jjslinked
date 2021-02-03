package one.xis.sql;

import java.util.List;

public interface CrudRepository<E,ID> {

    void save(E entity);

    E findById(ID id);

    List<E> findAll();

    void delete(E entity);


}
