package one.xis.sql.api;

import one.xis.sql.CrudRepository;

import java.util.Collection;
import java.util.List;

public class RepositoryBase<E, ID> implements CrudRepository<E, ID> {


    @Override
    public void save(E entity) {
    }

    @Override
    public void saveAll(Collection<E> entities) {
        
    }

    @Override
    public E findById(ID id) {
        return null;
    }

    @Override
    public List<E> findAll() {
        return null;
    }

    @Override
    public void delete(E entity) {

    }


}
