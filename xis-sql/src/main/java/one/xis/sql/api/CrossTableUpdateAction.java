package one.xis.sql.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
class CrossTableUpdateAction<EID, F, FID> {
    private final CrossTableAccessor<EID, FID> crossTableAccessor;

    void doUpdate(@NonNull EID entityKeyColumnValue, @NonNull Collection<FID> fieldValueKeys) {
        crossTableAccessor.replaceValues(entityKeyColumnValue, fieldValueKeys);
    }

}
