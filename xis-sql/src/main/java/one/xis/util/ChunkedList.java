package one.xis.util;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class ChunkedList<T> implements List<T> {

    @Delegate
    private final List<T> list;

    public ChunkedList(T... elements) {
        this(new ArrayList<>(Arrays.asList(elements)));
    }

    public ChunkedList(Collection<T> elements) {
        this(elements instanceof List ? ((List<T>) elements) : new ArrayList<>(elements));
    }

    public List<T> removeChunk(int maxSize) {
        int n = Math.min(maxSize, list.size());
        List<T> rv = new ArrayList<>(list.subList(0, n));
        list.subList(0, n).clear();
        return rv;
    }
}
