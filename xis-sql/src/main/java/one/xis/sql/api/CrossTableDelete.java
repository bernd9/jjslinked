package one.xis.sql.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class CrossTableDelete<EID, FID> {
    private final CrossTableAccessor<EID, FID> crossTableAccessor;

    void doAction(@NonNull EID entityPk) {
        crossTableAccessor.removeReferences(entityPk);
    }
}
