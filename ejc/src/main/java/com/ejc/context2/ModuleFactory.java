package com.ejc.context2;

import com.ejc.api.context.Module;
import com.ejc.processor.ModuleWriter;
import com.ejc.util.ClassUtils;
import lombok.Getter;

import java.util.*;

public abstract class ModuleFactory {

    @Getter
    private Module module = new Module();

    private ClassReference applicationClass;

    private final Set<com.ejc.context2.SingletonConstructor> constructors = new HashSet<>();
    private final Map<ClassReference, SingletonObject> singletonObjectMap = new HashMap<>();
    private final Set<ClassReference> classesToReplace = new HashSet<>();

    com.ejc.context2.Module getModule() {
        return new com.ejc.context2.Module(applicationClass.getReferencedClass(), constructors, singletonObjectMap, classesToReplace);
    }

    abstract void init();

    public static Optional<String> getPackageName(String appClassQualifiedName) {
        return ClassUtils.getPackageName(appClassQualifiedName);
    }

    public static String getSimpleName(String appClassQualifiedName) {
        return ClassUtils.getSimpleName(appClassQualifiedName) + "ModuleFactory";
    }

    @UsedInGeneratedCode(ModuleWriter.class)
    public void setApplicationClass(ClassReference applicationClass) {
        this.applicationClass = applicationClass;
    }

    @UsedInGeneratedCode(ModuleWriter.class)
    public void addInitMethod(ClassReference owner, String name) {
        getSingleton(owner).addInitMethod(new InitMethod(name));
    }

    @UsedInGeneratedCode(ModuleWriter.class)
    public void addBeanMethod(ClassReference owner, String name, ClassReference returnType, ClassReference... parameterTypes) {
        getSingleton(owner).addBeanMethod(new BeanMethod(name, returnType, Arrays.asList(parameterTypes)));
    }

    @UsedInGeneratedCode(ModuleWriter.class)
    public void addConfigField(ClassReference owner, String name, Class<?> fieldType, String key, String defaultValue, boolean mandatory) {
        getSingleton(owner).addConfigField(new ConfigField(owner, name, fieldType, key, defaultValue));
    }

    @UsedInGeneratedCode(ModuleWriter.class)
    public void addDependencyField(ClassReference owner, String name, ClassReference fieldType) {
        getSingleton(owner).addSimpleDependencyField(new SimpleDependencyField(name, fieldType));
    }

    @UsedInGeneratedCode(ModuleWriter.class) // TODO support arrays in new class ArrayDependencyField
    public void addCollectionDependencyField(ClassReference owner, String name, ClassReference fieldType) {
        getSingleton(owner).addCollectionDependencyField(new CollectionDependencyField(name, owner, fieldType));
    }

    @UsedInGeneratedCode(ModuleWriter.class)
    public void addConstructor(ClassReference owner, ClassReference... parameterTypes) {
        constructors.add(new SingletonConstructor(owner, Arrays.asList(parameterTypes)));
    }

    @UsedInGeneratedCode(ModuleWriter.class)
    public void addClassToReplace(ClassReference type) {
        classesToReplace.add(type);
    }

    public static String getQualifiedName(String appClassQualifiedName) {
        return getPackageName(appClassQualifiedName).map(name -> name + ".").orElse("") + getSimpleName(appClassQualifiedName);
    }

    private SingletonObject getSingleton(ClassReference type) {
        return singletonObjectMap.computeIfAbsent(type, t -> new SingletonObject(type));
    }

}
