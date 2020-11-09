package com.ejc.api.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Getter
@RequiredArgsConstructor
class Module {
    private final Class<?> applicationClass;
    private final Collection<SingletonConstructor> singletonConstructors;
    private final Map<ClassReference, SingletonObject> singletonObjects;
    private final Set<ClassReference> classesToReplace;

    @Override
    public int hashCode() {
        return applicationClass.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!Module.class.isInstance(obj)) {
            return false;
        }
        Module module = (Module) obj;
        return applicationClass.getName().equals(module.applicationClass.getName());
    }
}
