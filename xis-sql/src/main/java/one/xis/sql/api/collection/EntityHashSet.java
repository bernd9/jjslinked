package one.xis.sql.api.collection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public class EntityHashSet<E> extends HashSet<E> { // TODO implement EntityCollection

    protected final List<E> deletedValues = new ArrayList<>();

    @Override
    public boolean remove(Object o) {
        if (contains(o)) {
            super.remove(o);
            deletedValues.add((E) o);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean rv = false;
        for (Object o : c) {
            if (super.remove(o)) {
                rv = true;
                deletedValues.add((E) o);
            }
        }
        return rv;
    }


    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        boolean rv = false;
        List<E> remove = new ArrayList<>();
        for (E o : this) {
            if (filter.negate().test(o)) {
                rv = true;
                remove.add(o);
                deletedValues.add(o);
            }
        }
        super.removeAll(remove);
        return rv;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean rv = false;
        for (Object o : c) {
            if (contains(o)) {
                rv = true;
                super.remove(o);
                deletedValues.add((E) o);
            }
        }
        return rv;
    }

    @Override
    public void clear() {
        deletedValues.addAll(this);
        super.clear();
    }
}
