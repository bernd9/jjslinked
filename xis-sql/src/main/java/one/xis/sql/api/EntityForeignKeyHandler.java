package one.xis.sql.api;

import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
abstract class EntityForeignKeyHandler<E,EID,FK> {


    void updateFkReferencesByDelete(E entity) {

    }


    protected abstract Collection<FK> getForeignKeysFromEntity(E entity);
    protected abstract Collection<FK> getForeignFromDb(EID entityId);


}
