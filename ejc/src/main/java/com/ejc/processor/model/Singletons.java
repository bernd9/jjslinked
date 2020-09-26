package com.ejc.processor.model;

import com.ejc.api.context.ClassReference;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Singletons {

    public static String IMPLEMENTATION_SIMPLE_NAME = "SingletonsImpl";

    private Map<ClassReference, SingletonModel> singletonModels = new HashMap<>();

    public void addSingleton(ClassReference type) {
        singletonModels.put(type, new SingletonModel(type));
    }

    public void addConstructorParameter(ClassReference owner, ClassReference parameterType) {
        getSingletonModel(owner).ifPresent(model -> model.addConstructorParameter(parameterType));
    }

    public void addCollectionConstructorParameter(ClassReference owner, Class<? extends Collection> collectionType, ClassReference genericType) {
        getSingletonModel(owner).ifPresent(model -> model.addConstructorCollectionParameter(collectionType, genericType));
    }

    public void addBeanMethod(ClassReference owner, String name) {
        getSingletonModel(owner).ifPresent(model -> model.addBeanMethod(name));
    }

    public void addInitMethod(ClassReference owner, String name) {
        getSingletonModel(owner).ifPresent(model -> model.addInitMethod(name));
    }

    public void addConfigField(ClassReference owner, String name, ClassReference fieldType) {
        getSingletonModel(owner).ifPresent(model -> model.addConfigValueField(name, fieldType));
    }

    public void addDependencyField(ClassReference owner, String name, ClassReference fieldType) {
        getSingletonModel(owner).ifPresent(model -> model.addDependencyValueField(name, fieldType));
    }

    public void addCollectionDependencyField(ClassReference owner, String name, ClassReference fieldType, ClassReference genericType) {
        getSingletonModel(owner).ifPresent(model -> model.addCollectionDependencyValueField(name, fieldType, genericType));
    }

    Collection<SingletonModel> getSingletonModels() {
        return singletonModels.values();
    }

    private Optional<SingletonModel> getSingletonModel(ClassReference classReference) {
        return Optional.ofNullable(singletonModels.get(classReference));
    }
}
