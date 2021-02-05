package one.xis.sql.api;

import java.util.ArrayList;
import java.util.List;


class BatchSaveAction<E> {
    private final List<SaveAction<E>> saveActions = new ArrayList<>();

    void addSaveAction(SaveAction<E> saveAction) {
        saveActions.add(saveAction);
    }

    void doSave() {
        // TODO split insert and update or better replace ?
    }
}
