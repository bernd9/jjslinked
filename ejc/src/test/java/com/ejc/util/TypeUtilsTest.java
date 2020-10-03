package com.ejc.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

class TypeUtilsTest {

    private Collection<String> collection = new ArrayList<>();

    @Test
    void getGenericType() {
        Class<?> c = TypeUtils.getGenericType(collection.getClass());
    }
}