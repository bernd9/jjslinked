package one.xis.util;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

@UtilityClass
public class CollectionUtils {

    public <T> T getOnlyElement(Collection<T> coll) {
        Iterator<T> iter = coll.iterator();
        if (!iter.hasNext()) {
            throw new NoSuchElementException("expected exactly one element, but was empty");
        }
        T rv = iter.next();
        if (iter.hasNext()) {
            throw new IllegalStateException("expected exactly one element, but there are more ones");
        }
        return rv;
    }

    public <T> T getOnlyElement(Collection<T> coll, String descriptionForException) {
        Iterator<T> iter = coll.iterator();
        if (!iter.hasNext()) {
            throw new NoSuchElementException(descriptionForException + ": there must be exactly one, but none was found");
        }
        T rv = iter.next();
        if (iter.hasNext()) {
            throw new IllegalStateException(descriptionForException + ": there must be exactly one, but there are more");
        }
        return rv;
    }

    public <T> T getFirstOrThrow(Collection<T> coll) {
        return coll.iterator().next();
    }

    public <T> Optional<T> getFirst(Collection<T> coll) {
        return coll.isEmpty() ? Optional.empty() : Optional.of(coll.iterator().next());
    }
}
