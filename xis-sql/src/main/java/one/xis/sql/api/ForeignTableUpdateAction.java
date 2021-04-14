package one.xis.sql.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import one.xis.sql.CrudRepository;
import one.xis.sql.ForeignKeyAction;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
class ForeignTableUpdateAction<EID, F, FID> {
    private final ForeignKeyAction foreignKeyAction;
    private final Function<F, FID> fieldGetPkFunction;
    private final BiConsumer<F, EID> fieldSetPkConsumer;
    private final ForeignKeyAccessor<EID, FID> foreignKeyAccessor;
    private final CrudRepository<F, FID> fieldCrudRepository;

    void doUpdate(@NonNull EID foreignKeyValue, @NonNull Collection<F> retainOrCreate) {
        Collection<F> retainFieldEntities = getRetainFieldEntities(retainOrCreate, fieldGetPkFunction);
        handleObsoleteFieldValues(retainFieldEntities, fieldGetPkFunction);
        updateForeignKey(foreignKeyValue, retainFieldEntities);
        saveFieldValues(retainFieldEntities);
    }

    private void updateForeignKey(EID foreignKeyValue, Collection<F> retainFieldEntities) {
        retainFieldEntities.forEach(f -> fieldSetPkConsumer.accept(f, foreignKeyValue));
    }

    private void saveFieldValues(Collection<F> retainFieldEntities) {
        fieldCrudRepository.save(retainFieldEntities);
    }

    private void handleObsoleteFieldValues(Collection<F> retainFieldEntities, Function<F, FID> fieldGetPkFunction) {
        Stream<FID> retainFieldIds = getFieldIdsFrom(retainFieldEntities, fieldGetPkFunction);
        if (foreignKeyAction == ForeignKeyAction.CASCADE_API) {
            deleteRemovedFieldValues(retainFieldIds);
        } else if (foreignKeyAction == ForeignKeyAction.SET_NULL_API) {
            setFkToNullInRemovedFieldValues(retainFieldIds);
        }
    }

    private Collection<F> getRetainFieldEntities(Collection<F> retainOrCreate, Function<F, FID> fieldGetPkFunction) {
        return retainOrCreate.stream()
                .filter(fe -> fieldGetPkFunction.apply(fe) != null)
                .collect(Collectors.toSet());
    }

    private Stream<FID> getFieldIdsFrom(Collection<F> fieldValues, Function<F, FID> fieldGetPkFunction) {
        return fieldValues.stream().map(fieldGetPkFunction);
    }

    private void deleteRemovedFieldValues(Stream<FID> retainFieldIds) {
        foreignKeyAccessor.deleteWhereNotIn(retainFieldIds);
    }

    private void setFkToNullInRemovedFieldValues(Stream<FID> retainFieldIds) {
        foreignKeyAccessor.setFkToNullExcept(retainFieldIds);
    }
}
