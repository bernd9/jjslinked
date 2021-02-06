package one.xis.sql;

import java.util.Collection;
import java.util.List;

public interface CrudRepository<E, ID> {

    void save(E entity);

    void saveAll(Collection<E> entities);

    E findById(ID id);

    List<E> findAll();

    void delete(E entity);


}
