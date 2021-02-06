package one.xis.sql.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public abstract class CrossTableFieldHandler<E, F, EID, FID> {
    // TODO validate crosstable field must be a collection


    private final Class<EID> entityPkType;
    private final Class<FID> fieldPkType;
    private final CrossTableUpdateAction<EID, F, FID> crossTableUpdateAction;
    private final CrossTableDeleteAction<EID> crossTableDeleteAction;

    public CrossTableFieldHandler(Class<EID> entityPkType, Class<FID> fieldPkType) {
        this.entityPkType = entityPkType;
        this.fieldPkType = fieldPkType;
        this.crossTableDeleteAction = new CrossTableDeleteAction<>(getCrossTableName(), getEntityKeyNameInCrossTable(), entityPkType);
        this.crossTableUpdateAction = new CrossTableUpdateAction<>(getCrossTableName(),
                getEntityKeyNameInCrossTable(),
                entityPkType,
                getFieldKeyNameInCrossTable(),
                fieldPkType);
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
        crossTableUpdateAction.doUpdate(pk, fieldValues);
    }

    void onDeleteEntity(E entity) {
        crossTableDeleteAction.doAction(getEntityPk(entity));
    }

    protected abstract String getCrossTableName();

    protected abstract String getEntityKeyNameInCrossTable();

    protected abstract String getFieldKeyNameInCrossTable();

    protected abstract Collection<F> getFieldValues(E entity);

    protected abstract EID getEntityPk(E entity);


}
