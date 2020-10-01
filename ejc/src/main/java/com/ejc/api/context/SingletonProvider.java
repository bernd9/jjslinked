package com.ejc.api.context;

import com.ejc.api.context.model.CollectionConstructorParameter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
abstract class SingletonProvider {


    private final ClassReference type;
    private final List<ClassReference> parameterTypes;
    private final ApplicationContextInitializer initializer;
    private List<Parameter> parameters;

    // TODO invoke
    void init() {
        parameterTypes.forEach(this::addParameter);
    }

    public void setAllSingletonTypes(Set<Class<?>> types) {
        parameters.stream()
                .filter(CollectionConstructorParameter.class::isInstance)
                .map(CollectionConstructorParameter.class::cast)
                .forEach(parameter -> parameter.setAllSingletonTypes(types));
    }

    public void onSingletonCreated(Object o) {
        parameters.forEach(param -> param.onSingletonCreated(o));
        if (isParametersSatisfied()) {
            initializer.onSingletonCreated(create());
        }
    }

    private void addParameter(ClassReference parameterType) {
        if (Collections.class.isAssignableFrom(parameterType.getReferencedClass())) {

        } else {
            parameters.add(new SimpleParameter(parameterType));
        }
    }

    private boolean isParametersSatisfied() {
        return parameters.stream().noneMatch(parameter -> !parameter.isSatisfied());
    }


    abstract Object create();


    public Set<ClassReference> getSingletonTypes() {
        return Collections.singleton(type);
    }

    interface Parameter {

        void onSingletonCreated(Object o);

        boolean isSatisfied();
    }

    @RequiredArgsConstructor
    class SimpleParameter implements Parameter {
        private final ClassReference parameterType;
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

}
