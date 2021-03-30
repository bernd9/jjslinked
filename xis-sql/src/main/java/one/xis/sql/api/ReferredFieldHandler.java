package one.xis.sql.api;

import java.util.Collection;
import java.util.function.Consumer;

public abstract class ReferredFieldHandler<E, EID, F, FID> extends CollectionFieldHandler<E, EID, F, FID> {

    public ReferredFieldHandler(EntityTableAccessor<F, FID> fieldEntityTableAccessor, EntityFunctions<F, FID> fieldEntityFunctions, Class<E> entityType, Class<F> fieldType) {
        super(fieldEntityTableAccessor, fieldEntityFunctions, entityType, fieldType);
    }

    protected void unlinkBySetFkToNull(Collection<F> unlinkedFieldValues, EntityCrudHandlerSession crudHandlerSession) {
        unlinkedFieldValues.forEach(fieldValue -> addValueUpdateAction(fieldValue, crudHandlerSession));
    }

    protected void unlinkByDelete(Collection<F> unlinkedFieldValues, EntityCrudHandlerSession crudHandlerSession) {
        addBulDeleteAction(unlinkedFieldValues, crudHandlerSession);
    }

    private void addBulDeleteAction(Collection<F> unlinkedFieldValues, EntityCrudHandlerSession crudHandlerSession) {
        crudHandlerSession.addBulkDeleteAction(unlinkedFieldValues, getFieldEntityTableAccessor(), getFieldEntityFunctions());
    }

    private void addValueUpdateAction(F fieldValue, EntityCrudHandlerSession crudHandlerSession) {
        crudHandlerSession.addValueUpdateAction(fieldValue, getForeignKeyToNullConsumer(), getFieldEntityTableAccessor(), getFieldEntityFunctions());
    }

    protected Consumer<F> getForeignKeyToNullConsumer() {
        return fieldValue -> setFieldValueFk(fieldValue, null);
    }


    @Override
    protected void updateLinkColumnValue(F fieldValue, E entity) {
        setFieldValueFk(fieldValue, entity);
    }

    protected abstract void setFieldValueFk(F fieldValue, E foreignKey);
}
