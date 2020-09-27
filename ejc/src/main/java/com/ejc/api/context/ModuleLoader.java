package com.ejc.api.context;

import com.ejc.api.context.model.Singletons;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class ModuleLoader {

    private final Class<?> applicationClass;
    private Set<Singletons> modules;

    public void load() {
        ServiceLoader<Singletons> loader = ServiceLoader.load(Singletons.class);
        modules = loader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toSet());
    }
    
}
