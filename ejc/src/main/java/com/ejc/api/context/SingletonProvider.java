package com.ejc.api.context;

import com.ejc.Value;
import lombok.Data;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public abstract class SingletonProvider {

    private final ClassReference type;
    private final List<ParameterReference> parameterReferences;
    private final List<Parameter> parameters = new ArrayList<>();
    private Executable executable;

    protected void initParameters() {
        for (int i = 0; i < parameterReferences.size(); i++) {
            addParameter(parameterReferences.get(i), i);
        }
    }

    void onSingletonCreated(Object o) {
        parameters.forEach(parameter -> parameter.onSingletonCreated(o));
    }

    abstract Object provide();

    protected abstract Executable lookupExecutable();

    protected void addParameter(ParameterReference parameterReference, int index) {
        if (executable == null) {
            executable = lookupExecutable();
        }
        if (executable.getParameters()[index].isAnnotationPresent(Value.class)) {
            Value valueAnnotation = executable.getParameters()[index].getAnnotation(Value.class);
            parameters.add(new ConfigParameter(parameterReference.getClassReference(), valueAnnotation.value(), valueAnnotation.defaultValue(), valueAnnotation.mandatory()));
        } else if (Collection.class.isAssignableFrom(parameterReference.getClassReference().getReferencedClass())) {
            ClassReference genericType = parameterReference.getClassReference().getGenericType().orElseThrow(() -> new IllegalStateException("collection-parameter must have generic type "));
            // TODO field in exception-message
            // TODO ExceptionType ?
            parameters.add(new CollectionParameter(parameterReference.getClassReference(), genericType));
        } else {
            parameters.add(new SimpleParameter(parameterReference.getClassReference()));
        }
    }

    public boolean isSatisfied(SingletonProviders providers) {
        return parameters.stream()
                .noneMatch(parameter -> !parameter.isSatisfied(providers));
    }

    protected Class<?>[] parameterTypes() {
        return parameterReferences.stream()
                .map(ParameterReference::getClassReference)
                .map(ClassReference::getReferencedClass).toArray(Class<?>[]::new);
    }

    protected Object[] parameters() {
        return parameters.stream().map(Parameter::getValue).toArray(Object[]::new);
    }
}
