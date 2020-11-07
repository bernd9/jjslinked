package com.ejc.context2;

import com.ejc.api.context.ClassReference;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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


    public void onSingletonCreated(Object o) {
        parameters.forEach(param -> param.onSingletonCreated(o));
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
        return parameterTypes.stream().map(ClassReference::getReferencedClass).toArray(Class<?>[]::new);
    }

    protected Object[] parameters() {
        return parameters.stream().map(Parameter::getValue).toArray(Object[]::new);
    }


}
