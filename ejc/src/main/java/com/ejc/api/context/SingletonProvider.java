package com.ejc.api.context;

import com.ejc.context2.ClassReference;
import lombok.Data;

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

    public boolean isSatisfied(Collection<SingletonProvider> providers) {
        return parameters.stream()
                .noneMatch(parameter -> !parameter.isSatisfied(providers));
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
