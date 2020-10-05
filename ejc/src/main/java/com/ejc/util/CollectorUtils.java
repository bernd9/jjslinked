package com.ejc.util;

import lombok.experimental.UtilityClass;

import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@UtilityClass
public class CollectorUtils {

    public static <T> Collector<T, ?, T> toOnlyElement(Supplier<RuntimeException> exceptionSupplier) {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw exceptionSupplier.get();
                    }
                    return list.get(0);
                }
        );
    }

    public static <T> Collector<T, ?, T> toOnlyElement() {
        return toOnlyElement(() -> new IllegalArgumentException("not a singleton"));
    }
}
