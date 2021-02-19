package one.xis.sql.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
class CrossTableUpdate<EID, F, FID> {
    private final CrossTableAccessor<EID, FID> crossTableAccessor;

    void doUpdate(@NonNull EID entityKeyColumnValue, @NonNull List<FID> fieldValueKeys) {
        crossTableAccessor.replaceValues(entityKeyColumnValue, fieldValueKeys);
    }

}
