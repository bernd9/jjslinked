package one.xis.sql.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CrossTableFieldHandler<E, F, EID, FID, A extends CrossTableAccessor<EID, FID>> {
    // TODO validate crosstable field must be a collection

    private final CrossTableAccessor<EID, FID> crossTableAccessor;

    public CrossTableFieldHandler(CrossTableAccessor<EID, FID> crossTableAccessor) {
        this.crossTableAccessor = crossTableAccessor;
    }

    // TODO may be remove these methods. order may be static in generated code.
    boolean isBeforeEntity() {
        return false;
    }

    void onSaveEntity(E entity) {
        EID pk = getEntityPk(entity); // TODO check in foreign keys for type to be equal
        Objects.requireNonNull(pk);
        Collection<F> fieldValues = getFieldValues(entity);
        if (fieldValues == null) {
            fieldValues = Collections.emptySet();
        }
        Set<FID> fieldValuePks = getFieldValuesPks(fieldValues);
        crossTableAccessor.updateByFieldReferences(pk, fieldValuePks);
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
