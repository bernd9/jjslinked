package one.xis.sql;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CrudRepository<E, ID> {

    void save(E entity);

    void save(Collection<E> entities);

    Optional<E> findById(ID id);

    List<E> findAll();

    void delete(E entity);

    void deleteAll(Collection<E> entities);


}
