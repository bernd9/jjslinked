package one.xis.util;

import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@UtilityClass
public class CollectorUtils {

    public static <T> Collector<T, ?, T> toOnlyElement(String identifierForException) {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    switch (list.size()) {
                        case 0:
                            throw new IllegalStateException(identifierForException + ": no element");
                        case 1:
                            return list.get(0);
                        default:
                            throw new IllegalStateException(identifierForException + ": too many matches (" + list.size() + ")");
                    }
                }
        );
    }

    public static <T> Collector<T, ?, T> toOnlyElement() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    switch (list.size()) {
                        case 0:
                            throw new IllegalStateException("stream is empty");
                        case 1:
                            return list.get(0);
                        default:
                            throw new IllegalStateException("too many matches (" + list.size() + ")");
                    }
                }
        );
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
                            throw new IllegalStateException("too many matches : [" + list.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]");
                    }
                }
        );
    }
}
