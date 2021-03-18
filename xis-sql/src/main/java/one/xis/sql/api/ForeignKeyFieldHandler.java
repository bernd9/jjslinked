package one.xis.sql.api;

import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
abstract class ForeignKeyFieldHandler<E, EID, F, FID> {
    private final ForeignKeyAccessor<EID, FID> foreignKeyAccessor;


    void update(EID entityId, Collection<F> fieldValues) {

    }


    abstract FID fieldValueId(F fieldValue);

}
