package one.xis.sql.api;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class EntityForeignKeyFieldHandler<E, EID, F, FID> {

    private final EntityProxy<E, EID> entity;
    private final String fkColumnName;
    private final EntityTableAccessor<F, FID> fieldEntityTableAccessor;

    private Optional<F> value;

    F getValue() {
        if (value == null) {
            value = loadValue();
        }
        return value.orElse(null);
    }

    private EID getFk() {
        return entity.pk();
    }

    private Optional<F> loadValue() {
        List<F> result = fieldEntityTableAccessor.getByColumnValue(getFk(), fkColumnName, List.class);
        switch (result.size()) {
            case 0:
                return Optional.empty();
            case 1:
                return Optional.of(result.get(0));
            default:
                throw new IllegalStateException("too many results for " + fkColumnName);
        }
    }

}
