package com.ejc.api.context;

import lombok.Getter;

import java.util.*;

@Getter
public class Module {
    private Map<ClassReference, SingletonConstructor> singletonConstructors = new HashMap<>();
    private Map<ClassReference, Collection<BeanMethod>> beanMethods = new HashMap<>();
    private Map<ClassReference, Collection<InitMethodInvoker>> initInvokers = new HashMap<>();
    private Map<ClassReference, Collection<SimpleDependencyField>> dependencyFields = new HashMap<>();
    private Map<ClassReference, Collection<CollectionDependencyField>> collectionDependencyFields = new HashMap<>();
    private Map<ClassReference, Collection<ConfigValueField>> configFields = new HashMap<>();
    private Set<ClassReference> classesToReplace = new HashSet<>();
}
