package com.ejc.api.context;

import com.ejc.api.context.model.Singletons;
import com.ejc.util.CollectorUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class ModuleLoader {

    private final Class<?> applicationClass;
    private Singletons actualModule;
    private Set<Singletons> modules;

    public void load() {
        Set<Singletons> allSingleton = loadAllSingletons();
        String actualSingletonsName = Singletons.getQualifiedName(applicationClass.getName());
        actualModule = allSingleton.stream()
                .filter(singletons -> !singletons.getClass().getName().equals(actualSingletonsName))
                .collect(CollectorUtils.toSingleton(() -> new IllegalStateException("no singelton class")));
        modules = new HashSet<>(allSingleton);
        modules.remove(actualModule);
    }

    private Set<Singletons> loadAllSingletons() {
        ServiceLoader<Singletons> loader = ServiceLoader.load(Singletons.class);
        return loader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toSet());
    }

}
