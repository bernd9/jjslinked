package com.ejc.api.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class ModuleLoader {
    private final Class<?> applicationClass;

    public Set<ModuleFactory> load() {
        ServiceLoader<ModuleFactory> loader = ServiceLoader.load(ModuleFactory.class);
        return loader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toSet());
    }

}
