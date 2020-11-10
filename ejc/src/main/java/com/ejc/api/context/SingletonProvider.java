package com.ejc.api.context;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
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

    protected void addParameter(ClassReference parameterType) {
        if (Collections.class.isAssignableFrom(parameterType.getReferencedClass())) {
            parameters.add(new CollectionParameter(parameterType));
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
