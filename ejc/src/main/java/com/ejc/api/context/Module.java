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
}
