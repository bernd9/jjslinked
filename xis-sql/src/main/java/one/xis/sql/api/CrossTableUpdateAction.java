package one.xis.sql.api;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
class CrossTableUpdateAction<EID, F, FID> {
    private final String crossTableName;
    private final String entityKeyColumnName;
    private final Class<EID> entityKeyColumnType;
    private final String fieldKeyColumnName;
    private final Class<FID> fieldColumnType;

    void doUpdate(@NonNull EID entityKeyColumnValue, @NonNull Collection<F> fieldValues) {

    }

}
