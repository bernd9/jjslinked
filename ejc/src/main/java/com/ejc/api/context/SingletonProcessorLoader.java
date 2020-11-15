package com.ejc.api.context;

import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

class SingletonProcessorLoader {
    static Set<SingletonProcessor> load() {
        ServiceLoader<SingletonProcessor> loader = ServiceLoader.load(SingletonProcessor.class);
        return loader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toSet());
    }
}
