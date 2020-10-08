package com.ejc.util;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@UtilityClass
public class CollectorUtils {

    public static <T> Collector<T, ?, T> toOnlyElement(Function<Collection, RuntimeException> exceptionFunction) {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw exceptionFunction.apply(list);
                    }
                    return list.get(0);
                }
        );
    }

    public static <T> Collector<T, ?, T> toOnlyElement() {
        return toOnlyElement((list) -> new IllegalArgumentException("not a singleton: [" + list.stream().collect(Collectors.joining(", ")) + "]"));
    }
}
