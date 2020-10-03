package com.ejc.api.context;

import com.ejc.util.TypeUtils;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Data
public abstract class SingletonProvider {

    private final ClassReference type;
    private final List<ClassReference> parameterTypes;
    private final List<Parameter> parameters = new ArrayList<>();

    public SingletonProvider(ClassReference type, List<ClassReference> parameterTypes) {
        this.type = type;
        this.parameterTypes = parameterTypes;
        parameterTypes.forEach(this::addParameter);
    }

    public void registerSingletonTypes(Set<ClassReference> types) {
        parameters.stream()
                .filter(CollectionParameter.class::isInstance)
                .map(CollectionParameter.class::cast)
                .forEach(parameter -> parameter.registerSingletonTypes(types));
    }

    public void onSingletonCreated(Object o) {
        parameters.forEach(param -> param.onSingletonCreated(o));
        if (isSatisfied()) {
            ApplicationContextInitializer.getInstance().remove(this);
            ApplicationContextInitializer.getInstance().onSingletonCreated(invoke());
        }
    }

    public Object invoke() {
        return create();
    }

    protected abstract Object create();

    private void addParameter(ClassReference parameterType) {
        if (Collections.class.isAssignableFrom(parameterType.getReferencedClass())) {
            parameters.add(new CollectionParameter(parameterType));
        } else {
            parameters.add(new SimpleParameter(parameterType));
        }
    }

    public boolean isSatisfied() {
        return parameters.stream().noneMatch(parameter -> !parameter.isSatisfied());
    }

    protected Class<?>[] parameterTypes() {
        return parameterTypes.stream().map(ClassReference::getReferencedClass).toArray(Class<?>[]::new);
    }

    protected Object[] parameters() {
        return parameters.stream().map(Parameter::getValue).toArray(Object[]::new);
    }


    public Set<ClassReference> getSingletonTypes() {
        return Collections.singleton(type);
    }

    interface Parameter {

        void onSingletonCreated(Object o);

        boolean isSatisfied();

        Object getValue();
    }

    @RequiredArgsConstructor
    class SimpleParameter implements Parameter {
        private final ClassReference parameterType;

        @Getter
        private Object value;

        @Override
        public void onSingletonCreated(Object o) {
            if (parameterType.isInstance(o)) {
                if (value != null) {
                    // TODO Exception
                }
                value = o;
            }
        }

        @Override
        public boolean isSatisfied() {
            return value != null;
        }
    }


    class CollectionParameter implements Parameter {

        private final Collection<?> values;
        private int expectedElementCount;
        private Class<?> elementType;


        CollectionParameter(ClassReference collectionType) {
            this((Class<Collection<Object>>) collectionType.getReferencedClass());
        }

        CollectionParameter(Class<? extends Collection<?>> collectionType) {
            values = TypeUtils.emptyCollection(collectionType);
            elementType = TypeUtils.getGenericType(collectionType);
        }

        @Override
        public void onSingletonCreated(Object o) {

        }

        @Override
        public boolean isSatisfied() {
            return false;
        }

        @Override
        public Object getValue() {
            return null;
        }

        void registerSingletonTypes(Set<ClassReference> types) {
            expectedElementCount = (int) types.stream()
                    .filter(type -> type.getReferencedClass().isAssignableFrom(elementType))
                    .count();
        }
    }

}
