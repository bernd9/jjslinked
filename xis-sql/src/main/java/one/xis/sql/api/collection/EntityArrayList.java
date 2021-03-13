package one.xis.sql.api.collection;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@Getter
public class EntityArrayList<E> extends ArrayList<E> implements EntityCollection<E> {

    private final List<E> deletedValues = new ArrayList<>();
    private final List<E> newValues = new ArrayList<>();
    private final List<E> updateValues = new ArrayList<>();
    private boolean dirty;

    EntityArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public EntityArrayList() {
    }

    EntityArrayList(Collection<? extends E> c) {
        super(c);
    }

    @Override
    public boolean add(E e) {
        dirty = true;
        newValues.add(e);
        return super.add(e);
    }

    @Override
    public void add(int index, E element) {
        dirty = true;
        newValues.add(element);
        super.add(index, element);
    }


    @Override
    public boolean addAll(Collection<? extends E> c) {
        dirty = true;
        newValues.addAll(c);
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        dirty = true;
        newValues.addAll(c);
        return super.addAll(index, c);
    }

    @Override
    public boolean remove(Object o) {
        if (contains(o)) {
            super.remove(o);
            deletedValues.add((E) o);
            dirty = true;
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
                dirty = true;
                deletedValues.add((E) o);
            }
        }
        return rv;
    }

    @Override
    public E set(int index, E element) {
        E o = super.get(index);
        if (o != null) {
            dirty = true;
            deletedValues.add(o);
        }
        newValues.add(element);
        return super.set(index, element);
    }

    @Override
    public E remove(int index) {
        E o = super.remove(index);
        if (o != null) {
            dirty = true;
            deletedValues.add(o);
        }
        return o;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        this.subList(fromIndex, toIndex).stream()
                .filter(Objects::nonNull)
                .peek(e -> dirty = true)
                .forEach(deletedValues::add);
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        stream().filter(Objects::nonNull)
                .peek(e -> dirty = true)
                .forEach(deletedValues::add);
        super.replaceAll(operator);
        newValues.addAll(this);
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
            if (!contains(o)) {
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
