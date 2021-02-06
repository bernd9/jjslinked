package one.xis.sql.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class CrossTableDeleteAction<EID> {
    private final String crossTableName;
    private final String entityKeyColumnName;
    private final Class<EID> entityKeyColumnType;

    void doAction(@NonNull EID entityPk) {

    }
}
