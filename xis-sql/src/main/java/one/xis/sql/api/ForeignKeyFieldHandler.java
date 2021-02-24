package one.xis.sql.api;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ForeignKeyFieldHandler<E, EID, F, FID> {
    private final ForeignKeyAccessor<EID, FID> foreignKeyAccessor;
}
