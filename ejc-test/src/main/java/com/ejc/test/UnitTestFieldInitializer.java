package com.ejc.test;

import com.ejc.Inject;
import com.ejc.Value;
import com.ejc.util.CollectorUtils;
import com.ejc.util.FieldUtils;
import com.ejc.util.ParameterUtils;
import com.ejc.util.TypeUtils;
import lombok.RequiredArgsConstructor;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class UnitTestFieldInitializer {
    private final Object test;

    void setTestFieldValues() {
        Set<Object> providedDependencies = providedDependencies();
        setSingletonFieldsInTest(providedDependencies);
    }

    private void setSingletonFieldsInTest(Set<Object> providedDependencies) {
        FieldUtils.getAllFields(test).stream()
                .filter(field -> field.isAnnotationPresent(TestSubject.class))
                .forEach(fieldForSingleton -> {
                    Object singleton = createInstance(fieldForSingleton, providedDependencies);
                    setConfigFieldValues(singleton, fieldForSingleton);
                    setDependencyFieldValues(singleton, providedDependencies);
                    FieldUtils.setFieldValue(test, fieldForSingleton, singleton);
                });
    }

    private void setDependencyFieldValues(Object singleton, Set<Object> providedDependencies) {
        FieldUtils.getAllFields(singleton).stream()
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .forEach(field -> setDependencyFieldValue(singleton, field, providedDependencies));
    }

    private void setConfigFieldValues(Object singleton, Field fieldForSingleton) {
        Collection<Field> fieldsInSingleton = FieldUtils.getAllFields(singleton);
        setConfigFieldValues(fieldForSingleton, singleton, fieldsInSingleton);

    }

    private void setConfigFieldValues(Field fieldForSingleton, Object singleton, Collection<Field> fieldsInSingleton) {
        Map<String, String> configValues = configValues(fieldForSingleton);
        fieldsInSingleton.stream()
                .filter(fieldInSingleton -> fieldInSingleton.isAnnotationPresent(Value.class))
                .forEach(configField -> setConfigFieldValue(configField, singleton, configValues));
    }

    private void setConfigFieldValue(Field fieldInSingleton, Object singleton, Map<String, String> configValues) {
        Object configValue = getConfigValueOrThrow(fieldInSingleton.getAnnotation(Value.class), fieldInSingleton.getType(), configValues);
        FieldUtils.setFieldValue(singleton, fieldInSingleton, configValue);
    }

    private void setDependencyFieldValue(Object singleton, Field dependencyField, Set<Object> providedDependencies) {
        if (Collection.class.isAssignableFrom(dependencyField.getType())) {
            setCollectionDependencyFieldValue(singleton, dependencyField, providedDependencies);
        } else {
            setSimpleDependencyFieldValue(singleton, dependencyField, providedDependencies);
        }
    }

    private void setSimpleDependencyFieldValue(Object singleton, Field dependencyField, Set<Object> providedDependencies) {
        FieldUtils.setFieldValue(singleton, dependencyField, getSimpleDependency(dependencyField, providedDependencies));
    }

    private void setCollectionDependencyFieldValue(Object singleton, Field dependencyField, Set<Object> providedDependencies) {
        FieldUtils.setFieldValue(singleton, dependencyField, getCollectionDependency(dependencyField, providedDependencies));
    }

    private static Object getSimpleDependency(Field dependencyField, Set<Object> providedDependencies) {
        return providedDependencies.stream()
                .filter(dependencyField.getType()::isInstance)
                .collect(CollectorUtils.toOnlyElement("value for field " + dependencyField));
    }


    private static Object getSimpleDependency(Parameter parameter, Set<Object> providedDependencies) {
        return providedDependencies.stream()
                .filter(parameter.getType()::isInstance)
                .collect(CollectorUtils.toOnlyElement("value for parameter " + parameter));
    }

    private static Collection<Object> getCollectionDependency(Field dependencyField, Set<Object> providedDependencies) {
        Collection<Object> collection = TypeUtils.emptyCollection((Class<? extends Collection<Object>>) dependencyField.getType());
        Class<?> genericType = FieldUtils.getGenericCollectionType(dependencyField).orElseThrow(() -> new IllegalStateException(dependencyField + " must have a generic type argument"));
        collection.addAll(providedDependencies.stream().filter(genericType::isInstance).collect(Collectors.toSet()));
        return collection;
    }

    private static Collection<Object> getCollectionDependency(Parameter parameter, Set<Object> providedDependencies) {
        Collection<Object> collection = TypeUtils.emptyCollection((Class<? extends Collection<Object>>) parameter.getType());
        Class<?> genericType = ParameterUtils.getGenericCollectionType(parameter).orElseThrow(() -> new IllegalStateException(parameter + " must have a generic type argument"));
        collection.addAll(providedDependencies.stream().filter(genericType::isInstance).collect(Collectors.toSet()));
        return collection;
    }


    private Set<Object> providedDependencies() {
        Collection<Field> testFields = FieldUtils.getAllFields(test);
        Set<Object> dependencies = new HashSet<>();
        dependencies.addAll(testDependencies(testFields));
        dependencies.addAll(mocks(testFields));
        dependencies.addAll(spies(testFields));
        return dependencies;
    }

    Map<String, String> configValues(Field fieldForSingleton) {
        return Arrays.stream(fieldForSingleton.getAnnotationsByType(InjectConfigValue.class))
                .collect(Collectors.toMap(InjectConfigValue::name, InjectConfigValue::value));
    }

    private Set<Object> testDependencies(Collection<Field> testFields) {
        return testFields.stream()
                .filter(field -> field.isAnnotationPresent(TestDependency.class))
                .map(this::getFieldValueOrThrow)
                .collect(Collectors.toSet());
    }

    private Set<Object> mocks(Collection<Field> testFields) {
        return testFields.stream()
                .filter(field -> field.isAnnotationPresent(Mock.class))
                .map(this::setMockIfNull)
                .collect(Collectors.toSet());
    }

    private Set<Object> spies(Collection<Field> testFields) {
        return testFields.stream()
                .filter(field -> field.isAnnotationPresent(Spy.class))
                .map(this::setSpy)
                .collect(Collectors.toSet());
    }

    private Object getFieldValueOrThrow(Field field) {
        return Objects.requireNonNull(FieldUtils.getFieldValue(test, field), "value of " + field + " is null");
    }

    private Object setMockIfNull(Field field) {
        Object o = FieldUtils.getFieldValue(test, field);
        if (o == null) {
            o = Mockito.mock(field.getType());
            FieldUtils.setFieldValue(test, field, o);
        }
        return o;
    }

    private Object setSpy(Field field) {
        Object o = FieldUtils.getFieldValue(test, field);
        if (o == null) {
            o = Mockito.spy(field.getType());
            FieldUtils.setFieldValue(test, field, o);
        } else if (!MockUtil.isSpy(o)) {
            o = Mockito.spy(o);
            FieldUtils.setFieldValue(test, field, o);
        }
        return o;
    }

    private Object createInstance(Field fieldForSingleton, Set<Object> dependencies) {
        Map<String, String> configValues = configValues(fieldForSingleton);
        Object o;
        try {
            o = createInstance(fieldForSingleton.getType(), dependencies, configValues);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return o;
    }

    private Object createInstance(Class<?> type, Set<Object> dependencies, Map<String, String> configValues) throws Exception {
        Constructor<?> constructor = Arrays.stream(type.getDeclaredConstructors()).findFirst().orElseThrow();
        constructor.setAccessible(true);
        Object[] args = prepareArgs(constructor, dependencies, configValues);
        return constructor.newInstance(args);
    }

    private Object[] prepareArgs(Constructor<?> constructor, Set<Object> dependencies, Map<String, String> configValues) {
        Object[] args = new Object[constructor.getParameters().length];
        int index = 0;
        for (Parameter parameter : constructor.getParameters()) {
            if (parameter.isAnnotationPresent(Value.class)) {
                args[index++] = getConfigValueOrThrow(parameter.getAnnotation(Value.class), parameter.getType(), configValues);
            } else {
                args[index++] = getDependencyOrThrow(parameter, dependencies);
            }
        }
        return args;
    }

    private Object getDependencyOrThrow(Parameter parameter, Set<Object> dependencies) {
        if (Collection.class.isAssignableFrom(parameter.getType())) {
            return getCollectionDependency(parameter, dependencies);
        }
        return getSimpleDependency(parameter, dependencies);
    }
    
    private Object getConfigValueOrThrow(Value value, Class<?> type, Map<String, String> configValues) {
        String valueAsString;
        if (configValues.containsKey(value.value())) {
            valueAsString = configValues.get(value.value());
        } else if (!value.defaultValue().isEmpty()) {
            valueAsString = value.defaultValue();
        } else {
            throw new NullPointerException("no value of type " + type);
        }
        return TypeUtils.convertStringToSimple(valueAsString, type);
    }
}
