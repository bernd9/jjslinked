package one.xis.sql.api;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class CrossTableFieldHandler<E, F, EID, FID, A extends CrossTableAccessor<EID, FID>> {
    // TODO validate crosstable field must be a collection

    private final CrossTableUpdate<EID, F, FID> crossTableUpdate;
    private final CrossTableDelete<EID, FID> crossTableDelete;

    public CrossTableFieldHandler(CrossTableAccessor<EID, FID> crossTableAccessor) {
        this.crossTableDelete = new CrossTableDelete<>(crossTableAccessor);
        this.crossTableUpdate = new CrossTableUpdate<>(crossTableAccessor);
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
        crossTableUpdate.doUpdate(pk, getFieldValuesPks(fieldValues));
    }

    void onDeleteEntity(E entity) {
        crossTableDelete.doAction(getEntityPk(entity));
    }

    protected abstract Collection<F> getFieldValues(E entity);

    private List<FID> getFieldValuesPks(Collection<F> fieldValues) {
        return fieldValues.stream().map(this::getFieldValuePk).collect(Collectors.toList());
    }

    protected abstract EID getEntityPk(E entity);

    protected abstract FID getFieldValuePk(F fieldValue);

}
