package com.ejc.util;

import lombok.experimental.UtilityClass;

import java.util.Collection;

@UtilityClass
public class CollectionUtils {

    public <T> T getOnlyElement(Collection<T> coll) {
        return coll.iterator().next();
    }

}
