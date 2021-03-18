package one.xis.sql.api;

import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
abstract class EntityForeignKeyHandler<E,EID,FK> {


    protected void updateFkReferencesByDelete(E entity) {
        Collection<FK> foreignKeysEntity = getForeignKeysFromEntity(entity);
        Collection<FK> foreignKeysDb = getForeignKeysFromDb(getPk(entity));

    }

    private void deleteByFk(EID pk,  Collection<FK> fk) {

    }




    protected abstract Collection<FK> getForeignKeysFromEntity(E entity);
    protected abstract Collection<FK> getForeignKeysFromDb(EID entityId);
    protected abstract EID getPk(E entity);


}
