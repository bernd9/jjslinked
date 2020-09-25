package com.ejc.context2;

import com.ejc.api.context.ClassReference;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Getter
class SingletonElement {
    private final SingletonConstructor constructor;
    private final Set<InitMethod> initMethods = new HashSet<>();
    private final Set<BeanMethod> beanMethods = new HashSet<>();
    private final Set<ConfigValueField> configValueFields = new HashSet<>();
    private final Set<SingleDependencyField> singleDependencyFields = new HashSet<>();
    private Object singleton;

    public SingletonElement(ClassReference type, ClassReference... constructorParameters) {
        this.constructor = new SingletonConstructor(type, constructorParameters);
    }

    boolean isCreatable() {
        return constructor.isSatisfied();
    }

    boolean isDependenciesComplete() {
        return singleDependencyFields.stream().noneMatch(field -> !field.isFulfilled());
    }

    Object createSingleton() {
        singleton = constructor.invoke();
        return singleton;
    }

    void updateDependencyFields(Object singleton) {
        singleDependencyFields.forEach(field -> field.onSingletonCreated(singleton));
    }

    void invokeInitMethods() {
        initMethods.forEach(method -> method.invoke(singleton));
    }

    void injectConfigValues() {
        configValueFields.forEach(field -> field.doInject(singleton));
    }

    Stream<Object> invokeBeanFactoryMethods() {
        return beanMethods.stream().map(method -> method.invoke(singleton));
    }


}
