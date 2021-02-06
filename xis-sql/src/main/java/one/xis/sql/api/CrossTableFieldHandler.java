package one.xis.sql.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public abstract class CrossTableFieldHandler<E, F, EID, FID, A extends CrossTableAccessor<EID, FID>> {
    // TODO validate crosstable field must be a collection

    private final CrossTableUpdateAction<EID, F, FID> crossTableUpdateAction;
    private final CrossTableDeleteAction<EID, FID> crossTableDeleteAction;

    public CrossTableFieldHandler(CrossTableAccessor<EID, FID> crossTableAccessor) {
        this.crossTableDeleteAction = new CrossTableDeleteAction<>(crossTableAccessor);
        this.crossTableUpdateAction = new CrossTableUpdateAction<>(crossTableAccessor);
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
        crossTableUpdateAction.doUpdate(pk, getFieldValuesPks(fieldValues));
    }

    void onDeleteEntity(E entity) {
        crossTableDeleteAction.doAction(getEntityPk(entity));
    }

    protected abstract Collection<F> getFieldValues(E entity);

    protected abstract Collection<FID> getFieldValuesPks(Collection<F> fieldValues);

    protected abstract EID getEntityPk(E entity);
    
}
