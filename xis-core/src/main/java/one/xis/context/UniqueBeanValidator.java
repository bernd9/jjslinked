package one.xis.context;

import java.util.*;
import java.util.stream.Collectors;

public class UniqueBeanValidator {

    private Map<ClassReference, Set<Object>> uniqueBaseTypes = new HashMap();
    private Set<ClassReference> createdTypes = new HashSet<>();

    // TODO Map also fields and parameters for better exception messages

    UniqueBeanValidator(SingletonProviders singletonProviders, Collection<SimpleDependencyField> simpleDependencyFields) {
        singletonProviders.getProviders().stream()
                .map(SingletonProvider::getParameters)
                .flatMap(Collection::stream)
                .filter(SimpleParameter.class::isInstance)
                .map(SimpleParameter.class::cast)
                .map(SimpleParameter::getParameterType)
                .collect(Collectors.toSet())
                .forEach(classReference -> uniqueBaseTypes.put(classReference, null));
        simpleDependencyFields.stream().map(SimpleDependencyField::getFieldType)
                .forEach(classReference -> uniqueBaseTypes.put(classReference, null));
    }

    void onSingletonCreated(Object o) {
        uniqueBaseTypes.entrySet().stream()
                .filter(e -> e.getKey().isInstance(o))
                .forEach(e -> {
                    Set<Object> set = uniqueBaseTypes.computeIfAbsent(e.getKey(), t -> new HashSet<>());
                    if (!set.isEmpty()) {
                        throw new SingletonNotUniqueException();// TODO
                    }
                    set.add(o);
                });
        ClassReference type = ClassReference.getRef(o.getClass().getName());
        if (createdTypes.contains(type)) {
            throw new SingletonNotUniqueException();// TODO may be another exception
        }
        createdTypes.add(type);
    }

}
