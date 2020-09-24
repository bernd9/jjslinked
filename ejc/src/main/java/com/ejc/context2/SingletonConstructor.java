package com.ejc.context2;

import com.ejc.api.context.ClassReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode
@RequiredArgsConstructor
public class SingletonConstructor {

    @Getter
    private final ClassReference type;
    private List<ConstructorDependency> constructorDependencies;

    SingletonConstructor(ClassReference type, ClassReference... dependencies) {
        this.type = type;
        this.constructorDependencies = Arrays.stream(dependencies)
                .map(ConstructorDependency::new)
                .collect(Collectors.toList());
    }

    boolean isSatisfied() {
        return constructorDependencies.stream().noneMatch(dep -> !dep.isSatisfied());
    }

    void onSingletonCreated(Object o) {
        constructorDependencies.forEach(dep -> dep.onSingletonCreated(o));
    }

    Object invoke() {
        try {
            return type.getReferencedClass()
                    .getDeclaredConstructor(parameterClasses())
                    .newInstance(parameters());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?>[] parameterClasses() {
        return constructorDependencies.stream()
                .map(ConstructorDependency::getType)
                .map(ClassReference::getReferencedClass)
                .toArray(Class[]::new);
    }

    private Object[] parameters() {
        return constructorDependencies.stream()
                .map(ConstructorDependency::getDependency)
                .toArray(Object[]::new);
    }


    @Setter
    @Getter
    @RequiredArgsConstructor
    class ConstructorDependency {
        private final ClassReference type;
        private Object dependency;

        void onSingletonCreated(Object o) {
            if (type.isInstance(o)) {
                dependency = o;
            }
        }

        boolean isSatisfied() {
            return dependency != null;
        }
    }

}
