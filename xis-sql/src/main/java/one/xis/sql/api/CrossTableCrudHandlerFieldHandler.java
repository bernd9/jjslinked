package one.xis.sql.api;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class CrossTableCrudHandlerFieldHandler<E, EID, F, FID> extends CollectionFieldHandler<E, EID, F, FID> {
    // TODO validate crosstable field must be a collection

    private final CrossTableAccessor<EID, FID> crossTableAccessor;

    public CrossTableCrudHandlerFieldHandler(EntityTableAccessor<F, FID> fieldEntityTableAccessor, CrossTableAccessor<EID, FID> crossTableAccessor, EntityFunctions<F, FID> fieldEntityFunctions, Class<E> entityType, Class<F> fieldType) {
        super(fieldEntityTableAccessor, fieldEntityFunctions, entityType, fieldType);
        this.crossTableAccessor = crossTableAccessor;
    }

    @Override
    protected void handleUnlinkedFieldValues(EID entityId, Collection<F> unlinkedFieldValues, EntityCrudHandlerSession crudHandlerSession) {
        crossTableAccessor.deleteReferences(entityId, unlinkedFieldValues.stream().map(this::getFieldValuePk));
    }

    @Override
    protected void doUpdateFieldValuesBySessionState(E entity, Map<Integer, F> sessionFieldValueMap, Map<Integer, F> actualFieldValueMap, EntityCrudHandlerSession crudHandlerSession) {
        super.doUpdateFieldValuesBySessionState(entity, sessionFieldValueMap, actualFieldValueMap, crudHandlerSession);
        crossTableAccessor.addReferences(getEntityPk(entity), getNewValues(sessionFieldValueMap, actualFieldValueMap).stream().map(this::getFieldValuePk));
    }

    private Collection<F> getNewValues(Map<Integer, F> sessionFieldValueMap, Map<Integer, F> actualFieldValueMap) {
        return actualFieldValueMap.keySet().stream()
                .filter(sessionHashCode -> !sessionFieldValueMap.containsKey(sessionHashCode))
                .map(sessionFieldValueMap::get).collect(Collectors.toSet());
    }

    protected abstract FID getFieldValuePk(F fieldValue);

}
