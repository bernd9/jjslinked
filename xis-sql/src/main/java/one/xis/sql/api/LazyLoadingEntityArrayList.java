package one.xis.sql.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public class LazyLoadingEntityArrayList<E> extends ArrayList<E> implements EntityCollection<E> {

    protected final List<E> deletedValues = new ArrayList<>();
    protected final List<E> newValues = new ArrayList<>();
    private final Supplier<List<E>> supplier;

    private boolean loaded;
    private boolean dirty;

    @Override
    public void trimToSize() {
        ensureLoaded();
        super.trimToSize();
    }

    @Override
    public int size() {
        ensureLoaded();
        return super.size();
    }

    @Override
    public boolean isEmpty() {
        ensureLoaded();
        return super.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        ensureLoaded();
        return super.contains(o);
    }

    @Override
    public int indexOf(Object o) {
        ensureLoaded();
        return super.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        ensureLoaded();
        return super.lastIndexOf(o);
    }

    @Override
    public Object clone() {
        ensureLoaded();
        return super.clone();
    }

    @Override
    public Object[] toArray() {
        ensureLoaded();
        return super.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        ensureLoaded();
        return super.toArray(a);
    }

    @Override
    public E get(int index) {
        ensureLoaded();
        return super.get(index);
    }

    @Override
    public boolean add(E e) {
        ensureLoaded();
        dirty = true;
        newValues.add(e);
        return super.add(e);
    }

    @Override
    public void add(int index, E element) {
        ensureLoaded();
        dirty = true;
        newValues.add(element);
        super.add(index, element);
    }

    @Override
    public boolean equals(Object o) {
        ensureLoaded();
        return super.equals(o);
    }


    @Override
    public boolean addAll(Collection<? extends E> c) {
        ensureLoaded();
        dirty = true;
        newValues.addAll(c);
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        ensureLoaded();
        dirty = true;
        newValues.addAll(c);
        return super.addAll(index, c);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        ensureLoaded();
        return super.listIterator(index);
    }

    @Override
    public ListIterator<E> listIterator() {
        ensureLoaded();
        return super.listIterator();
    }

    @Override
    public Iterator<E> iterator() {
        ensureLoaded();
        return super.iterator();
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        ensureLoaded();
        return super.subList(fromIndex, toIndex);
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        ensureLoaded();
        super.forEach(action);
    }

    @Override
    public Spliterator<E> spliterator() {
        ensureLoaded();
        return super.spliterator();
    }

    @Override
    public void sort(Comparator<? super E> c) {
        ensureLoaded();
        super.sort(c);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        ensureLoaded();
        return super.containsAll(c);
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        ensureLoaded();
        return super.toArray(generator);
    }

    @Override
    public Stream<E> stream() {
        ensureLoaded();
        return super.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        ensureLoaded();
        return super.parallelStream();
    }

    @Override
    public boolean remove(Object o) {
        ensureLoaded();
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
        ensureLoaded();
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
        ensureLoaded();
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
        ensureLoaded();
        E o = super.remove(index);
        if (o != null) {
            dirty = true;
            deletedValues.add(o);
        }
        return o;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        ensureLoaded();
        this.subList(fromIndex, toIndex).stream()
                .filter(Objects::nonNull)
                .peek(e -> dirty = true)
                .forEach(deletedValues::add);
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        ensureLoaded();
        stream().filter(Objects::nonNull)
                .peek(e -> dirty = true)
                .forEach(deletedValues::add);
        super.replaceAll(operator);
        newValues.addAll(this);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        ensureLoaded();
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
        ensureLoaded();
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
        ensureLoaded();
        deletedValues.addAll(this);
        super.clear();
    }

    private synchronized void ensureLoaded() {
        if (!loaded) {
            super.addAll(supplier.get());
            loaded = true;
        }
    }

    @Override
    public void addSilently(E entity) {
        ensureLoaded();
        super.add(entity);
    }
}
