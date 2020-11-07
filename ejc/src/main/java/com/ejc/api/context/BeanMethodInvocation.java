package com.ejc.api.context;

import com.ejc.context2.ClassReference;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
class BeanMethodInvocation {
    private final Map<ClassReference, Collection<BeanMethod>> beanMethods = new HashMap<>();
    private final ApplicationContextInitializer initializer;

    void addBeanMethods(Map<ClassReference, Collection<BeanMethod>> beanMethods) {
        this.beanMethods.putAll(beanMethods);
    }

    void onSingletonCreated(Object o) {
        beanMethods.keySet().forEach(ownerClass -> onSingletonCreated(ownerClass, o));
    }

    private void onSingletonCreated(ClassReference ownerClass, Object o) {
        Collection<BeanMethod> methodsToInvoke = beanMethods.getOrDefault(ownerClass, Collections.emptySet()).stream()
                .peek(beanMethod -> beanMethod.onSingletonCreated(o))
                .filter(BeanMethod::isSatisfied)
                .collect(Collectors.toSet());
        invokeBeanMethods(ownerClass, methodsToInvoke);
    }

    void onDependenciesInjected(Object o) {
        ClassReference reference = ClassReference.getRef(o.getClass().getName());
        Collection<BeanMethod> methodsToInvoke = beanMethods.getOrDefault(reference, Collections.emptySet()).stream()
                .filter(BeanMethod::setOwnerFieldsInjected)
                .collect(Collectors.toSet());
        invokeBeanMethods(reference, methodsToInvoke);

    }

    private void invokeBeanMethods(ClassReference reference, Collection<BeanMethod> invokeMethods) {
        beanMethods.getOrDefault(reference, Collections.emptySet()).removeAll(invokeMethods);
        invokeMethods.stream().map(BeanMethod::invoke).forEach(initializer::onSingletonCreated);
    }

    void remove(ClassReference type) {
        beanMethods.remove(type);
    }

    Stream<BeanMethod> getAllBeanMethods() {
        return beanMethods.values().stream()
                .flatMap(Collection::stream);
    }
}
