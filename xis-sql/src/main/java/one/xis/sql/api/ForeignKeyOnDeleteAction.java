package one.xis.sql.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import one.xis.sql.ForeignKeyAction;

@RequiredArgsConstructor
public class ForeignKeyOnDeleteAction<EID, FID> {
    private final ForeignKeyAction foreignKeyAction;
    private final ForeignKeyAccessor<EID, FID> foreignKeyAccessor;

    void doAction(@NonNull EID foreignKeyValue) {
        if (foreignKeyAction == ForeignKeyAction.CASCADE_API) {
            deleteAllChildNodes(foreignKeyValue);
        } else if (foreignKeyAction == ForeignKeyAction.SET_NULL_API) {
            childNodeFkToNull(foreignKeyValue);
        }
    }

    private void deleteAllChildNodes(EID foreignKeyValue) {
        foreignKeyAccessor.deleteAllChildNodes(foreignKeyValue);
    }

    private void childNodeFkToNull(EID foreignKeyValue) {
        foreignKeyAccessor.setFkToNullForAllChildNodes(foreignKeyValue);
    }
}
