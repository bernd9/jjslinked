package com.ejc.api.context;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public abstract class SingletonProvider {

    private final ClassReference type;
    private final List<Parameter> parameters = new ArrayList<>();

    public SingletonProvider(ClassReference type, List<ClassReference> parameterTypes) {
        this.type = type;
        parameterTypes.forEach(this::addParameter);
    }

    void onSingletonCreated(Object o) {
        parameters.forEach(parameter -> parameter.onSingletonCreated(o));
    }

    abstract Object provide();

    protected void addParameter(ClassReference parameterType) {
        if (Collection.class.isAssignableFrom(parameterType.getReferencedClass())) {
            ClassReference genericType = parameterType.getGenericType().orElseThrow(() -> new IllegalStateException("collection-parameter must have generic type "));
            // TODO field in exception-message
            // TODO ExceptionType ?
            parameters.add(new CollectionParameter(parameterType, genericType));
        } else {
            parameters.add(new SimpleParameter(parameterType));
        }
    }

    public boolean isSatisfied(SingletonProviders providers) {
        return parameters.stream()
                .noneMatch(parameter -> !parameter.isSatisfied(providers));
    }

    protected Class<?>[] parameterTypes() {
        return parameters.stream()
                .map(Parameter::getParameterType)
                .map(ClassReference::getReferencedClass).toArray(Class<?>[]::new);
    }

    protected Object[] parameters() {
        return parameters.stream().map(Parameter::getValue).toArray(Object[]::new);
    }
}
