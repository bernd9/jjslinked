package com.ejc.processor;

import lombok.experimental.UtilityClass;

import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
class CustomSingletonAnnotationLoader {

    Set<String> load() {
        ServiceLoader<CustomSingletonAnnotationProvider> loader = ServiceLoader.load(CustomSingletonAnnotationProvider.class);
        return loader.stream().map(ServiceLoader.Provider::get)
                .map(CustomSingletonAnnotationProvider::getAnnotationClass)
                .collect(Collectors.toSet());
    }
}
