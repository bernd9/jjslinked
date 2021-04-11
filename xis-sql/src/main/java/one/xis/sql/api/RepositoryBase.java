package one.xis.sql.api;

import lombok.RequiredArgsConstructor;
import one.xis.sql.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class RepositoryBase<E, EID> implements CrudRepository<E, EID> {

    private final EntityCrudHandler<E,EID> entityCrudHandler;

    @Override
    public void save(E entity) {
        entityCrudHandler.save(entity);
    }

    @Override
    public void saveAll(Collection<E> entities) {
        entityCrudHandler.save(entities);
    }

    @Override
    public Optional<E> findById(EID eid) {
        return entityCrudHandler.findById(eid);
    }

    @Override
    public List<E> findAll() {
        return entityCrudHandler.findAll();
    }

    @Override
    public void delete(E entity) {
        entityCrudHandler.delete(entity);
    }

    @Override
    public void deleteAll(Collection<E> entities) {
        entityCrudHandler.deleteAll(entities);
    }




}
