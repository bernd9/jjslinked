package com.ejc.util;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@UtilityClass
public class CollectorUtils {

    public static <T> Collector<T, ?, T> toOnlyElement(Function<List<?>, RuntimeException> exceptionFunction) {
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
        return toOnlyElement((list) -> new IllegalArgumentException("not a singleton: [" + list.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]"));
    }

    public static <T> Collector<T, ?, Optional<T>> toOnlyOptional() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    switch (list.size()) {
                        case 0:
                            return Optional.empty();
                        case 1:
                            return Optional.of(list.get(0));
                        default:
                            throw new IllegalArgumentException("too many matches : [" + list.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]");
                    }
                }
        );
    }
}
