package com.ejc.context2;

import com.ejc.api.context.ClassReference;
import com.ejc.processor.UsedInGeneratedCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

public class ApplicationContextInitializerBase {

    private final Map<ClassReference, SingletonElement> newSingletons = new HashMap<>();

    @Getter
    private final Set<Object> singletonsCreated = new HashSet<>();

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void addBeanClass(ClassReference c, ClassReference... constructorParameters) {
        newSingletons.put(c, new SingletonElement(c, constructorParameters));
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void addConfigurationClass(ClassReference c, ClassReference... constructorParameters) {
        newSingletons.put(c, new SingletonElement(c, constructorParameters));
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected <B> void addImplementation(ClassReference c, ClassReference... constructorParameters) {
        newSingletons.put(c, new SingletonElement(c, constructorParameters));
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void addSingleValueDependency(ClassReference declaringClass, String fieldName, ClassReference fieldType) {
        newSingletons.get(declaringClass).getDependencyFields().add(new DependencyField(declaringClass, fieldName, fieldType));
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void addMultiValueDependency(ClassReference declaringClass, String fieldName, Class<?> fieldType, ClassReference elementType) {
        //multiValueInjectors.add(new MultiValueInjector(declaringClass, fieldName, fieldType, elementType));
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void addInitMethod(ClassReference declaringClass, String methodName) {
        newSingletons.get(declaringClass).getInitMethods().add(new InitMethod(declaringClass, methodName));
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void addLoadBeanMethod(ClassReference declaringClass, String methodName) {
        newSingletons.get(declaringClass).getBeanMethods().add(new BeanMethod(declaringClass, methodName));
    }


    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected void addConfigValueField(ClassReference declaringClass, String fieldName, Class<?> fieldType, String key, String defaultValue) {
        newSingletons.get(declaringClass).getConfigValueFields().add(new ConfigValueField(declaringClass, fieldName, fieldType, key, defaultValue));
    }

    void initialize() {
        SingletonStatusEventPublisher eventPublisher = new SingletonStatusEventPublisher();
        SingletonInstantiation singletonInstantiation = new SingletonInstantiation(eventPublisher, newSingletons.values());
        Singletons singletons = new Singletons();
        eventPublisher.addListener(singletons);
        eventPublisher.addListener(new ConfigFieldInjector(eventPublisher));
        eventPublisher.addListener(new InitMethodInvoker(eventPublisher));
        eventPublisher.addListener(new DependencyFieldInjector(eventPublisher));
        eventPublisher.addListener(new BeanMethodInvoker(eventPublisher, singletonInstantiation));
        singletonInstantiation.runInstantiation();
        singletonsCreated.addAll(singletons.getValues());
    }


    @RequiredArgsConstructor
    class SingletonInstantiation {
        private final SingletonStatusEventPublisher publisher;
        private final Collection<SingletonElement> singletons;

        void runInstantiation() {
            Set<SingletonElement> singletonsToCreate = singletons.stream()
                    .filter(SingletonElement::isCreatable)
                    .collect(Collectors.toSet());
            singletons.removeAll(singletonsToCreate);
            singletonsToCreate.stream()
                    .peek(SingletonElement::createSingleton)
                    .forEach(this::singletonCreated);
        }

        void onBeanCreatedInConfig(Object o) {
            if (!singletons.isEmpty()) {
                singletons.forEach(e -> e.onSingletonCreated(o));
                runInstantiation();
            }
        }

        private void singletonCreated(SingletonElement singletonElement) {
            singletons.forEach(e -> e.onSingletonCreated(singletonElement.getSingleton()));
            publisher.publish(new SingletonStatusEvent(singletonElement, SingletonStatus.INSTANTIATED));
        }
    }

    @RequiredArgsConstructor
    class ConfigFieldInjector implements SingletonStatusEventListener {

        private final SingletonStatusEventPublisher publisher;

        @Override
        public void onStatusEvent(SingletonStatusEvent e) {
            e.getSingletonElement().injectConfigValues();
            publisher.publish(new SingletonStatusEvent(e.getSingletonElement(), SingletonStatus.CONFIG_VALUES_SET));
        }

        @Override
        public SingletonStatus getSupportedStatus() {
            return SingletonStatus.INSTANTIATED;
        }
    }

    @RequiredArgsConstructor
    class DependencyFieldInjector implements SingletonStatusEventListener {

        private final SingletonStatusEventPublisher publisher;
        private final Set<SingletonElement> singletons = new HashSet<>();

        @Override
        public void onStatusEvent(SingletonStatusEvent event) {
            singletons.add(event.getSingletonElement());
            Set<SingletonElement> singletonsComplete = singletons.stream()
                    .filter(SingletonElement::isDependenciesComplete)
                    .collect(Collectors.toSet());
            singletons.removeAll(singletonsComplete);
            singletonsComplete.stream()
                    .map(e -> new SingletonStatusEvent(e, SingletonStatus.DEPENDENCY_FIELDS_SET))
                    .forEach(publisher::publish);
        }

        @Override
        public SingletonStatus getSupportedStatus() {
            return SingletonStatus.CONFIG_VALUES_SET;
        }
    }

    @RequiredArgsConstructor
    class InitMethodInvoker implements SingletonStatusEventListener {

        private final SingletonStatusEventPublisher publisher;

        @Override
        public void onStatusEvent(SingletonStatusEvent event) {
            event.getSingletonElement().invokeInitMethods();
            publisher.publish(new SingletonStatusEvent(event.getSingletonElement(), SingletonStatus.INIT_METHODS_INVOCATED));
        }

        @Override
        public SingletonStatus getSupportedStatus() {
            return SingletonStatus.DEPENDENCY_FIELDS_SET;
        }
    }

    @RequiredArgsConstructor
    class BeanMethodInvoker implements SingletonStatusEventListener {
        private final SingletonStatusEventPublisher publisher;
        private final SingletonInstantiation singletonInstantiation;

        @Override
        public void onStatusEvent(SingletonStatusEvent event) {
            event.getSingletonElement().invokeBeanFactoryMethods()
                    .forEach(singletonInstantiation::onBeanCreatedInConfig);
            publisher.publish(new SingletonStatusEvent(event.getSingletonElement(), SingletonStatus.BEAN_METHODS_INVOCATED));
        }

        @Override
        public SingletonStatus getSupportedStatus() {
            return SingletonStatus.INIT_METHODS_INVOCATED;
        }
    }

    @RequiredArgsConstructor
    class Singletons implements SingletonStatusEventListener {

        @Getter
        private final Set<Object> values = new HashSet<>();

        @Override
        public void onStatusEvent(SingletonStatusEvent event) {
            values.add(event.getSingletonElement().getSingleton());
        }

        @Override
        public SingletonStatus getSupportedStatus() {
            return SingletonStatus.BEAN_METHODS_INVOCATED;
        }
    }

    enum SingletonStatus {
        INSTANTIATED,
        CONFIG_VALUES_SET,
        DEPENDENCY_FIELDS_SET,
        INIT_METHODS_INVOCATED,
        BEAN_METHODS_INVOCATED
    }

    @Getter
    @RequiredArgsConstructor
    class SingletonStatusEvent {
        private final SingletonElement singletonElement;
        private final SingletonStatus singletonStatus;
    }

    interface SingletonStatusEventListener {
        void onStatusEvent(SingletonStatusEvent event);

        SingletonStatus getSupportedStatus();
    }

    class SingletonStatusEventPublisher {
        private Map<SingletonStatus, SingletonStatusEventListener> listeners = new HashMap<>();

        void addListener(SingletonStatusEventListener listener) {
            if (listeners.containsKey(listener.getSupportedStatus())) throw new IllegalStateException();
            listeners.put(listener.getSupportedStatus(), listener);
        }

        void publish(SingletonStatusEvent event) {
            listeners.get(event.getSingletonStatus()).onStatusEvent(event);
        }
    }
}
