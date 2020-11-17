package com.ejc.test;

import com.ejc.Inject;
import com.ejc.Value;
import com.ejc.util.CollectorUtils;
import com.ejc.util.FieldUtils;
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
    private final UnitTestContext unitTestContext;

    void setTestFieldValues() {
        Set<Object> providedDependencies = providedDependencies();
        setTestSubjectFieldsInTest(providedDependencies);
    }

    private void setTestSubjectFieldsInTest(Set<Object> providedDependencies) {
        unitTestContext.getTestAnnotatedFields().getOrDefault(InjectTestDependencies.class, Collections.emptySet())
                .forEach(field -> setTestSubjectFieldValueInTest(field, providedDependencies));
    }

    private void setTestSubjectFieldValueInTest(Field fieldForSingleton, Set<Object> providedDependencies) {
        Object singleton = setSingletonInstanceFieldInTestIfNull(fieldForSingleton, providedDependencies);
        Collection<Field> fieldsInSingleton = FieldUtils.getAllFields(singleton);
        setConfigFieldValues(fieldForSingleton, singleton, fieldsInSingleton);
        setDependencyFieldValues(singleton, fieldsInSingleton, providedDependencies);
    }

    private void setConfigFieldValues(Field fieldForSingleton, Object singleton, Collection<Field> fieldsInSingleton) {
        Map<String, String> configValues = configValues(fieldForSingleton);
        fieldsInSingleton.stream()
                .filter(fieldInSingleton -> fieldInSingleton.isAnnotationPresent(Value.class))
                .forEach(field -> setConfigFieldValue(fieldForSingleton, singleton, configValues));
    }

    private void setConfigFieldValue(Field fieldInSingleton, Object singleton, Map<String, String> configValues) {
        Object configValue = getConfigValueOrThrow(fieldInSingleton.getAnnotation(Value.class), fieldInSingleton.getType(), configValues);
        FieldUtils.setFieldValue(singleton, fieldInSingleton, configValue);
    }

    private void setDependencyFieldValues(Object singleton, Collection<Field> fieldsInSingleton, Set<Object> providedDependencies) {
        fieldsInSingleton.stream()
                .filter(fieldInSingleton -> fieldInSingleton.isAnnotationPresent(Inject.class))
                .forEach(dependencyField -> setDependencyFieldValue(singleton, dependencyField, providedDependencies));
    }

    private void setDependencyFieldValue(Object singleton, Field dependencyField, Set<Object> providedDependencies) {
        if (Collection.class.isAssignableFrom(dependencyField.getType())) {
            setCollectionDependencyFieldValue(singleton, dependencyField, providedDependencies);
        } else {
            setSimpleDependencyFieldValue(singleton, dependencyField, providedDependencies);
        }
    }

    private void setSimpleDependencyFieldValue(Object singleton, Field dependencyField, Set<Object> providedDependencies) {
        Object value = providedDependencies.stream()
                .filter(dependencyField.getType()::isInstance)
                .collect(CollectorUtils.toOnlyElement(list -> throwUnexpectedCountOfCandidates(dependencyField, list)));
        FieldUtils.setFieldValue(singleton, dependencyField, value);
    }

    private RuntimeException throwUnexpectedCountOfCandidates(Field dependencyField, List<?> dependencies) {
        if (dependencies.isEmpty()) {
            return new RuntimeException("unsatisfied dependency " + dependencyField);
        }
        if (dependencies.size() > 1) {
            return new RuntimeException("ambiguous values for " + dependencyField);
        }
        return new IllegalStateException();
    }

    @SuppressWarnings("unchecked")
    private void setCollectionDependencyFieldValue(Object singleton, Field dependencyField, Set<Object> providedDependencies) {
        Collection<Object> collection = TypeUtils.emptyCollection((Class<? extends Collection<Object>>) dependencyField.getType());
        Class<?> genericType = TypeUtils.getGenericType((Class<? extends Collection<?>>) dependencyField.getType());
        collection.addAll(providedDependencies.stream().filter(genericType::isInstance).collect(Collectors.toSet()));
        FieldUtils.setFieldValue(singleton, dependencyField, collection);
    }

    private Set<Object> providedDependencies() {
        Set<Object> dependencies = new HashSet<>();
        dependencies.addAll(testDependencies());
        dependencies.addAll(mocks());
        dependencies.addAll(spies());
        return dependencies;
    }

    Map<String, String> configValues(Field fieldForSingleton) {
        return Arrays.stream(fieldForSingleton.getAnnotationsByType(InjectConfigValue.class))
                .collect(Collectors.toMap(InjectConfigValue::name, InjectConfigValue::value));
    }

    private Set<Object> testDependencies() {
        return unitTestContext.getTestAnnotatedFields().getOrDefault(TestDependency.class, Collections.emptySet())
                .stream()
                .map(this::getFieldValueOrThrow)
                .collect(Collectors.toSet());
    }

    private Set<Object> mocks() {
        return unitTestContext.getTestAnnotatedFields().getOrDefault(Mock.class, Collections.emptySet())
                .stream()
                .map(this::setMockIfNull)
                .collect(Collectors.toSet());
    }

    private Set<Object> spies() {
        return unitTestContext.getTestAnnotatedFields().getOrDefault(Spy.class, Collections.emptySet())
                .stream()
                .map(this::setSpyIfNull)
                .collect(Collectors.toSet());
    }

    private Object getFieldValueOrThrow(Field field) {
        return Objects.requireNonNull(FieldUtils.getFieldValue(unitTestContext.getTest(), field), "value of " + field + " is null");
    }

    private Object setMockIfNull(Field field) {
        Object o = FieldUtils.getFieldValue(unitTestContext.getTest(), field);
        if (o == null) {
            o = Mockito.mock(field.getType());
            FieldUtils.setFieldValue(unitTestContext.getTest(), field, o);
        }
        return o;
    }

    private Object setSpyIfNull(Field field) {
        Object o = FieldUtils.getFieldValue(unitTestContext.getTest(), field);
        if (o == null) {
            o = Mockito.spy(field.getType());
            FieldUtils.setFieldValue(unitTestContext.getTest(), field, o);
        } else if (!MockUtil.isSpy(o)) {
            o = Mockito.spy(o);
            FieldUtils.setFieldValue(unitTestContext.getTest(), field, o);
        }
        return o;
    }

    private Object setSingletonInstanceFieldInTestIfNull(Field fieldForSingleton, Set<Object> dependencies) {
        Map<String, String> configValues = configValues(fieldForSingleton);
        Object o = FieldUtils.getFieldValue(unitTestContext.getTest(), fieldForSingleton);
        if (o == null) {
            try {
                o = createInstance(fieldForSingleton.getType(), dependencies, configValues);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            FieldUtils.setFieldValue(unitTestContext.getTest(), fieldForSingleton, o);
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
                args[index++] = getDependencyOrMock(parameter.getType(), dependencies);
            }
        }
        return args;
    }

    private Object getDependencyOrMock(Class<?> type, Set<Object> dependencies) {
        Optional<Object> dependency = dependencies.stream()
                .filter(o -> type.isInstance(o))
                .collect(CollectorUtils.toOnlyOptional());
        return dependency.orElseGet(() -> Mockito.mock(type));
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
