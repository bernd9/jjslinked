package com.ejc.processor;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@NoArgsConstructor
public class InternalEvents {

    private static Map<Class<?>, Consumer<?>> consumers = new HashMap<>();

    public static void fireEvent(Object model) {
        consumers.entrySet().stream()
                .filter(e -> e.getKey().isInstance(model))
                .map(Map.Entry::getValue)
                .map(Consumer.class::cast)
                .forEach(consumer -> consumer.accept(model));
    }

    public static <T> void onEvent(Consumer<T> consumer, Class<T> eventType) {
        consumers.put(eventType, consumer);
    }
}
