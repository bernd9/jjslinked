package com.ejc.api.context;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

    public boolean onSingletonCreated(Object o) {
        return parameters.stream()
                .filter(param -> param.onSingletonCreated(o))
                .count() == parameters.size();
    }

    public Object invoke() {
        return create();
    }

    protected abstract Object create();

    protected void addParameter(ClassReference parameterType) {
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


}
