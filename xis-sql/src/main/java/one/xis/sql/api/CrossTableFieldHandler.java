package one.xis.sql.api;

import lombok.NonNull;
import one.xis.sql.CrudRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CrossTableFieldHandler<E, F, EID, FID> {
    // TODO validate crosstable field must be a collection

    private final CrossTableAccessor<EID, FID> crossTableAccessor;
    private final CrudRepository<E, EID> entityRepository;

    private final CrudRepository<F, FID> fieldRepository;

    public CrossTableFieldHandler(CrossTableAccessor<EID, FID> crossTableAccessor, CrudRepository<E, EID> entityRepository, CrudRepository<F, FID> fieldRepository) {
        this.crossTableAccessor = crossTableAccessor;
        this.entityRepository = entityRepository;
        this.fieldRepository = fieldRepository;
    }

    // TODO may be remove these methods. order may be static in generated code.
    boolean isBeforeEntity() {
        return false;
    }

    void onSaveEntity(E entity) {
        @NonNull EID pk = getEntityPk(entity); // TODO check in foreign keys for type to be equal
        Collection<F> fieldValues = getFieldValues(entity);
        if (fieldValues == null) {
            fieldValues = Collections.emptySet();
        }
        Set<FID> fieldValuePks = getFieldValuesPks(fieldValues);
        crossTableAccessor.updateByFieldReferences(pk, fieldValuePks);
        entityRepository.save(entity);
        fieldRepository.saveAll(fieldValues);
    }

    void onDeleteEntity(E entity) {
        crossTableAccessor.removeAllReferences(getEntityPk(entity));
    }

    protected abstract Collection<F> getFieldValues(E entity);

    private Set<FID> getFieldValuesPks(Collection<F> fieldValues) {
        return fieldValues.stream().map(this::getFieldValuePk).collect(Collectors.toSet());
    }

    protected abstract EID getEntityPk(E entity);

    protected abstract FID getFieldValuePk(F fieldValue);
}
