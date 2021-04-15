package one.xis.sql.api;

import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
public class EntityForeignKeyCollectionFieldHandler<E, EID, F, FID, C extends Collection<F>> {

    private final EntityProxy<E, EID> entity;
    private final String fkColumnName;
    private final EntityTableAccessor<F, FID> fieldEntityTableAccessor;
    private final Class<C> collectionType;

    private C values;

    C getValues() {
        if (values == null) {
            values = loadValues();
        }
        return values;
    }

    private EID getFk() {
        return entity.pk();
    }

    private C loadValues() {
        return fieldEntityTableAccessor.getByColumnValue(getFk(), fkColumnName, collectionType);
    }

}
