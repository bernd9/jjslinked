package com.ejc.test;

import com.ejc.Inject;
import com.ejc.api.context.SingletonPreProcessor;
import com.ejc.util.CollectorUtils;
import com.ejc.util.FieldUtils;
import com.ejc.util.TypeUtils;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class IntegrationTestInitializer extends SingletonPreProcessor<Object> {
    private final Object test;

    private Set<Field> injectFields;
    private Set<Field> mockFields;
    private Set<Field> spyFields;

    public IntegrationTestInitializer(Object test) {
        super(Object.class);
        this.test = test;
        init();
    }

    private void init() {
        findInjectFields();
        findMockFields();
        findSpyFields();
        prepareInjectCollectionFields();
    }

    private void findInjectFields() {
        injectFields = FieldUtils.getAllFields(test).stream()
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .collect(Collectors.toSet());
    }

    private void findMockFields() {
        mockFields = FieldUtils.getAllFields(test).stream()
                .filter(field -> field.isAnnotationPresent(Mock.class))
                .collect(Collectors.toSet());
    }

    private void findSpyFields() {
        spyFields = FieldUtils.getAllFields(test).stream()
                .filter(field -> field.isAnnotationPresent(Spy.class))
                .collect(Collectors.toSet());
    }


    private void prepareInjectCollectionFields() {
        injectFields.stream()
                .filter(field -> Collection.class.isAssignableFrom(field.getType()))
                .forEach(this::bindEmptyCollection);

    }

    private void bindEmptyCollection(Field field) {
        Collection<Object> coll = TypeUtils.emptyCollection((Class<Collection<Object>>) field.getType());
        FieldUtils.setFieldValue(test, field, coll);
    }


    @Override
    public Optional<Object> beforeInstantiation(Class<Object> type) {
        Optional<Object> singleton = bindMock(type);
        if (singleton.isPresent()) {
            return singleton;
        }
        singleton = bindSpy(type);
        if (singleton.isPresent()) {
            return singleton;
        }
        return Optional.empty();
    }

    @Override
    public Object afterInstantiation(Object o) {
        injectIntoSimpleField(o);
        addToInjectCollectionField(o);
        return o;
    }

    @SuppressWarnings("unused")
    void setTestFieldValue(Object o) {
        Objects.requireNonNull(this.injectFields, "not initialized");
        injectIntoSimpleField(o);
        addToInjectCollectionField(o);
    }


    private void injectIntoSimpleField(Object o) {
        injectFields.stream()
                .filter(field -> field.getType().isAssignableFrom(o.getClass()))
                .forEach(field -> FieldUtils.setFieldValue(test, field, o));
    }


    private void addToInjectCollectionField(Object o) {
        injectFields.stream()
                .filter(field -> Collection.class.isAssignableFrom(field.getType()))
                .filter(field -> matchesGenericTypeParameter(field, o))
                .map(field -> FieldUtils.getFieldValue(test, field))
                .map(Collection.class::cast)
                .forEach(coll -> coll.add(o));
    }

    private boolean matchesGenericTypeParameter(Field field, Object o) {
        Class<?> genericType = FieldUtils.getGenericCollectionType(field).orElseThrow(() -> new IllegalStateException(field + " must have a generic type parameter"));
        return genericType.isInstance(o);
    }

    private Optional<Object> bindMock(Class<?> type) {
        return getMockFieldForType(type)
                .map(this::bindMock)
                .map(Optional::of)
                .orElse(Optional.empty());
    }

    private Object bindMock(Field field) {
        Object fieldValue = FieldUtils.getFieldValue(test, field);
        if (fieldValue == null) {
            fieldValue = Mockito.mock(field.getType());
            FieldUtils.setFieldValue(test, field, fieldValue);
        }
        if (!MockUtil.isMock(fieldValue)) {
            throw new IllegalStateException("not a mock: " + fieldValue);
        }
        return fieldValue;
    }

    private Optional<Field> getMockFieldForType(Class<?> type) {
        return mockFields.stream()
                .filter(field -> field.getType().isAssignableFrom(type))
                .collect(CollectorUtils.toOnlyOptional());
    }

    private Optional<Object> bindSpy(Class<?> type) {
        return getSpyFieldForType(type)
                .map(this::bindSpy)
                .map(Optional::of)
                .orElse(Optional.empty());
    }

    private Object bindSpy(Field field) {
        Object fieldValue = FieldUtils.getFieldValue(test, field);
        if (fieldValue == null) {
            fieldValue = Mockito.spy(field.getType());
            FieldUtils.setFieldValue(test, field, fieldValue);
        }
        if (!MockUtil.isSpy(fieldValue)) {
            fieldValue = Mockito.spy(fieldValue);
        }
        return fieldValue;
    }

    private Optional<Field> getSpyFieldForType(Class<?> type) {
        return spyFields.stream()
                .filter(field -> field.getType().isAssignableFrom(type))
                .collect(CollectorUtils.toOnlyOptional());
    }
}
