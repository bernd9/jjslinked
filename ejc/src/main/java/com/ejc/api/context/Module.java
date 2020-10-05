package com.ejc.api.context;

import lombok.Getter;

import java.util.*;

@Getter
public class Module {
    private final Collection<SingletonConstructor> singletonConstructors = new HashSet<>();
    private final Map<ClassReference, Collection<BeanMethod>> beanMethods = new HashMap<>();
    private final Map<ClassReference, Collection<InitMethodInvoker>> initInvokers = new HashMap<>();
    private final Collection<SimpleDependencyField> dependencyFields = new HashSet<>();
    private final Map<ClassReference, Collection<CollectionDependencyField>> collectionDependencyFields = new HashMap<>();
    private final Map<ClassReference, Collection<ConfigValueField>> configFields = new HashMap<>();
    private final Set<ClassReference> classesToReplace = new HashSet<>();
}
