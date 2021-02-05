package one.xis.sql.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@Getter
@RequiredArgsConstructor
public class EntityArrayList<E> extends ArrayList<E> {

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
    public E set(int index, E element) {
        E o = super.get(index);
        if (o != null) {
            deletedValues.add(o);
        }
        return super.set(index, element);
    }

    @Override
    public E remove(int index) {
        E o = super.remove(index);
        if (o != null) {
            deletedValues.add(o);
        }
        return o;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        this.subList(fromIndex, toIndex).stream()
            .filter(Objects::nonNull)
            .forEach(deletedValues::add);
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        stream().filter(Objects::nonNull)
                .forEach(deletedValues::add);
        super.replaceAll(operator);
        stream().filter(Objects::nonNull)
                .forEach(deletedValues::remove);
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
