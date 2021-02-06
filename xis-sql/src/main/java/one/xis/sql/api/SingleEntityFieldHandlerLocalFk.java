package one.xis.sql.api;

import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
abstract class SingleEntityFieldHandlerLocalFk<E, F, FID> {

    private final String fieldName;
    private final Class<F> fieldClass;

    boolean isBeforeEntity() {
        return true;
    }

    void onSaveEntity(E entity, SaveAction<E> entitySaveAction) {
        F fieldValue = getFieldValue(entity);
        FID fieldValuePk;
        if (fieldValue == null) {
            fieldValuePk = null;
        } else {
            SaveAction<F> saveAction = new SaveAction<>(fieldValue, fieldClass);
            saveAction.doSave();
            fieldValuePk = getFieldValuePk(fieldValue);
            Objects.requireNonNull(fieldValuePk);
        }
        entitySaveAction.getValues().put(getForeignColumnKeyName(), fieldValuePk);
        entitySaveAction.doSave();
    }

    void onDeleteEntity(E entity) {
        // NOOP
    }

    protected abstract F getFieldValue(E entity);

    protected abstract FID getFieldValuePk(F fieldValue);


    protected abstract String getForeignColumnKeyName();

    protected abstract boolean isExplicitDelete();

    protected abstract boolean isExplicitSetNull();
}
