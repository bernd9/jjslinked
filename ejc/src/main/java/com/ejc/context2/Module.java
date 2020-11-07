package com.ejc.context2;

import com.ejc.api.context.ClassReference;
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
