package com.ejc.util;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

@UtilityClass
public class CollectionUtils {

    public <T> T getOnlyElement(Collection<T> coll) {
        Iterator<T> iter = coll.iterator();
        if (!iter.hasNext()) {
            throw new NoSuchElementException("expected exactly one element, but was empty");
        }
        T rv = iter.next();
        if (iter.hasNext()) {
            throw new NoSuchElementException("expected exactly one element, but there are more ones");
        }
        return rv;
    }

}
