package com.ejc.api.context;

import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class CompoundSingletonProcessor implements SingletonProcessor {

    private final Set<SingletonProcessor> singletonProcessors;

    @Override
    public Optional<Object> beforeInstantiation(Class<?> type) {
        return singletonProcessors.stream()
                .map(singletonProcessor -> singletonProcessor.beforeInstantiation(type))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    @Override
    public Optional<Object> afterInstantiation(Object o) {
        return singletonProcessors.stream()
                .map(singletonProcessor -> singletonProcessor.afterInstantiation(o))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    void addSingletonProcessor(SingletonProcessor singletonProcessor) {
        singletonProcessors.add(singletonProcessor);
    }


}
