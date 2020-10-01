package com.ejc.api.context;

import com.ejc.api.context.model.ConfigValueField;
import com.ejc.api.context.model.DependencyField;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ApplicationContextInitializer {

    private Set<ConstructorSingletonProvider> constructorSingletonProviders = new HashSet<>();
    private Map<ClassReference, Collection<BeanMethodSingletonProvider>> beanMethodSingeltonProviders = new HashMap<>();
    private Map<ClassReference, Collection<InitMethodInvoker>> initInvokers = new HashMap<>();
    private Map<ClassReference, Collection<DependencyField>> dependencyFields = new HashMap<>();
    private Map<ClassReference, Collection<ConfigValueField>> configFields = new HashMap<>();

    private Set<Object> singletons = new HashSet<>();

    public void initialize() {
        Set<Class<?>> allSingletonTypes = constructorSingletonProviders.stream()
                .map(SingletonProvider::getSingletonTypes)
                .flatMap(Set::stream)
                .map(ClassReference::getReferencedClass)
                .collect(Collectors.toSet());
        constructorSingletonProviders.forEach(provider -> provider.setAllSingletonTypes(allSingletonTypes));
    }

    public void onSingletonCreated(Object o) {
        injectConfigFields(o);
        if (dependencyFieldsComplete(o)) {
            invokeInitMethods(o);
            invokeBeanMethods(o);
        }
        constructorSingletonProviders.forEach(provider -> provider.onSingletonCreated(o));
        dependencyFields.values().stream()
                .flatMap(Collection::stream)
                .forEach(field -> field.onSingletonCreated(o));
        singletons.add(o);
    }

    public void onDependencyFieldComplete(Object o) {
        if (dependencyFieldsComplete(o)) {
            invokeInitMethods(o);
            invokeBeanMethods(o);
        }
    }

    private void invokeBeanMethods(Object o) {
        ClassReference reference = ClassReference.getRef(o.getClass().getName());
        beanMethodSingeltonProviders.getOrDefault(reference, Collections.emptySet()).stream()
                .map(BeanMethodSingletonProvider::create)
                .forEach(this::onSingletonCreated);
    }

    private void invokeInitMethods(Object o) {
        ClassReference reference = ClassReference.getRef(o.getClass().getName());
        initInvokers.getOrDefault(reference, Collections.emptySet())
                .forEach(invoker -> invoker.doInvokeMethod(o));
    }

    private void injectConfigFields(Object o) {
        ClassReference reference = ClassReference.getRef(o.getClass().getName());
        configFields.getOrDefault(reference, Collections.emptySet())
                .forEach(ConfigValueField::injectConfigValue);
    }

    private boolean dependencyFieldsComplete(Object o) {
        ClassReference reference = ClassReference.getRef(o.getClass().getName());
        return dependencyFields.getOrDefault(reference, Collections.emptySet())
                .stream().noneMatch(field -> !field.isSatisfied());
    }


}


