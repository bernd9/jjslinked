package one.xis.sql.api;

import com.ejc.api.context.UsedInGeneratedCode;
import com.google.common.base.Functions;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.collection.EntityCollection;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class ReferredFieldHandler<E, EID, F, FID> {
    private final EntityCrudHandler<F, FID> entityCrudHandler;
    private final String foreignKeyColumnName;

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public void updateFieldValues(E entity, F fieldValue, EntityCrudHandlerSession session) {
        updateFieldValues(entity, Collections.singleton(fieldValue), session);
    }

    @UsedInGeneratedCode
    public void updateFieldValues(E entity, Collection<F> fieldValues, EntityCrudHandlerSession session) {
        if (fieldValues instanceof EntityCollection) {
            updateCollectionProxyFieldValues(entity, (EntityCollection) fieldValues);
        } else {
            updateCollectionFieldValues(entity, fieldValues);
        }
    }

    private void updateCollectionProxyFieldValues(E entity, EntityCollection<F> fieldValues) {
        if (fieldValues.isDirty()) {
            unlinkFieldValues(fieldValues.stream().map(this::getFieldValuePk).collect(Collectors.toList()));
            saveFieldValues(entity, fieldValues.getDirtyValues());
        }
    }

    private void updateCollectionFieldValues(E entity, Collection<F> fieldValues) {
        Collection<FID> fieldPksInDb = entityCrudHandler.getEntityTableAccessor().getPksByColumnValue(getEntityPk(entity), foreignKeyColumnName);
        Map<FID,F> fieldValueMap = asMap(fieldValues);
        unlinkObsoleteFieldValues(fieldPksInDb, fieldValueMap);
        saveFieldValues(entity, fieldValues);
    }

    private void unlinkObsoleteFieldValues(Collection<FID> fieldPksInDb, Map<FID,F> fieldValueMap) {
        Collection<FID> unlinkFieldValuePks = getUnlinkFieldValuePks(fieldPksInDb, fieldValueMap);
        unlinkFieldValues(unlinkFieldValuePks);
    }

    private void saveFieldValues(E entity, Collection<F> fieldValues) {
        fieldValues.forEach(value -> setFieldValueFk(value, entity));
        entityCrudHandler.save(fieldValues);
    }

    private Collection<FID> getUnlinkFieldValuePks(Collection<FID> fieldPksInDb, Map<FID,F> fieldValueMap) {
        Collection<FID> unlinkFieldIds = new HashSet<>(fieldPksInDb);
        unlinkFieldIds.removeAll(fieldValueMap.keySet());
        return unlinkFieldIds;
    }

    private Map<FID,F> asMap(Collection<F> entities) {
        return entities.stream().collect(Collectors.toMap(this::getFieldValuePk, Functions.identity()));
    }

    protected void unlinkByFkFkToNull(Collection<FID> fieldPks) {
        entityCrudHandler.getEntityTableAccessor().updateColumnValuesToNull(fieldPks, foreignKeyColumnName);
    }


    protected void unlinkByDelete(Collection<FID> fieldPks) {
        entityCrudHandler.getEntityTableAccessor().deleteAllById(fieldPks);
    }

    protected void unlinkBySetFkToNull(Collection<FID> fieldPks) {
        entityCrudHandler.getEntityTableAccessor().updateColumnValuesToNull(fieldPks, foreignKeyColumnName);

    }

    protected abstract EID getEntityPk(E entity);

    protected abstract FID getFieldValuePk(F fieldValue);

    protected abstract void setFieldValueFk(F fieldValue, E entity);


    // depends on delete cascade behaviour
    protected abstract void unlinkFieldValues(Collection<FID> fieldPks);



}
