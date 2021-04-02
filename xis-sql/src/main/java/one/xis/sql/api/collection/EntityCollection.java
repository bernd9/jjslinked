package one.xis.sql.api.collection;


import java.util.Collection;
import java.util.List;

public interface EntityCollection<E> extends Collection<E> {
    boolean isDirty();

    Collection<E> getUnlinkedValues();

    Collection<E> getDirtyValues();

    Collection<E> getNewValues();

    Class<E> getElementType();
}
