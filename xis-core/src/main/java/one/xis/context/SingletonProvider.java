package one.xis.context;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public abstract class SingletonProvider {

    private final ClassReference type;
    private final List<ParameterReference> parameterReferences;
    private final List<Parameter> parameters = new ArrayList<>();

    public SingletonProvider(ClassReference type, List<ParameterReference> parameterReferences) {
        this.type = type;
        this.parameterReferences = parameterReferences;
        this.parameterReferences.forEach(this::addParameter);
    }

    void onSingletonCreated(Object o) {
        parameters.forEach(parameter -> parameter.onSingletonCreated(o));
    }

    abstract Object provide();

    protected void addParameter(ParameterReference parameterReference) {
        if (parameterReference.getValueAnnotationReference().isPresent()) {
            ValueAnnotationReference valueRef = parameterReference.getValueAnnotationReference().get();
            parameters.add(new ConfigParameter(parameterReference.getClassReference(), valueRef.getKey(), valueRef.getDefaultValue(), valueRef.isMandatory()));
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
