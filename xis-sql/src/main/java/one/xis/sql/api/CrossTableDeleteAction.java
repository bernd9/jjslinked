package one.xis.sql.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class CrossTableDeleteAction<EID, FID> {
    private final CrossTableAccessor<EID, FID> crossTableAccessor;

    void doAction(@NonNull EID entityPk) {
        crossTableAccessor.removeReferences(entityPk);
    }
}
