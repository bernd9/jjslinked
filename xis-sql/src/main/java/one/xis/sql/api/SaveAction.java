package one.xis.sql.api;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
class SaveAction<E> {
    private final Map<String, Object> values = new HashMap<>();
    private final E entity;
    private final Class<E> entityClass;


    SaveAction(E entity, Class<E> entityClass) {
        this.entity = entity;
        this.entityClass = entityClass;
        // TODO put into map ?
    }

    void doSave() {

    }

}
