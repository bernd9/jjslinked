package one.xis.sql.api;

import com.ejc.api.context.UsedInGeneratedCode;
import com.google.common.base.Functions;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.collection.EntityCollection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class ReferredFieldHandler<EID, F, FID, P extends EntityProxy<F, FID>> {
    private final EntityCrudHandler<F, FID, P> entityCrudHandler;
    private final String foreignKeyColumnName;

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public void updateFieldValues(EID entityId, F fieldValue) {
        updateFieldValues(entityId, Collections.singleton(fieldValue));
    }

    @UsedInGeneratedCode
    public void updateFieldValues(EID entityId, Collection<F> fieldValues) {
        if (fieldValues instanceof EntityCollection) {
            updateCollectionProxyFieldValues(entityId, (EntityCollection) fieldValues);
        } else {
            updateCollectionFieldValues(entityId, fieldValues);
        }
    }

    private void updateCollectionProxyFieldValues(EID entityId, EntityCollection<F> fieldValues) {
        if (fieldValues.isDirty()) {
            unlinkFieldValues(fieldValues.stream().map(this::getFieldValuePk).collect(Collectors.toList()));
            saveFieldValues(entityId, fieldValues.getDirtyValues());
        }
    }

    private void updateCollectionFieldValues(EID entityId, Collection<F> fieldValues) {
        Collection<FID> fieldPksInDb = entityCrudHandler.getEntityTableAccessor().getPksByColumnValue(entityId, foreignKeyColumnName);
        Map<FID,F> fieldValueMap = asMap(fieldValues);
        unlinkObsoleteFieldValues(fieldPksInDb, fieldValueMap);
        saveFieldValues(entityId, fieldValues);
    }

    private void unlinkObsoleteFieldValues(Collection<FID> fieldPksInDb, Map<FID,F> fieldValueMap) {
        Collection<FID> unlinkFieldValuePks = getUnlinkFieldValuePks(fieldPksInDb, fieldValueMap);
        unlinkFieldValues(unlinkFieldValuePks);
    }

    private void saveFieldValues(EID entityId, Collection<F> fieldValues) {
        fieldValues.forEach(value -> setFk(value, entityId));
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

    protected abstract FID getFieldValuePk(F fieldValue);

    // depends on delete cascade behaviour
    protected abstract void unlinkFieldValues(Collection<FID> fieldPks);

    protected void unlinkByDelete(Collection<FID> fieldPks) {
        entityCrudHandler.getEntityTableAccessor().deleteAllById(fieldPks);
    }

    protected void unlinkBySetFkToNull(Collection<FID> fieldPks) {
        entityCrudHandler.getEntityTableAccessor().updateColumnValuesToNull(fieldPks, foreignKeyColumnName);

    }

    protected abstract void setFk(F fieldValue, EID fk);



}
