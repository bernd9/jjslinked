package com.ejc.api.context;

import com.ejc.util.ClassUtils;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public abstract class ModuleFactory {

    private final ClassReference applicationClass;

    private final Set<SingletonConstructor> constructors = new HashSet<>();
    private final Map<ClassReference, SingletonObject> singletonObjectMap = new HashMap<>();
    private final Map<ClassReference, ClassReference> classReplacements = new HashMap<>();

    public Module getModule() {
        return new Module(applicationClass.getReferencedClass(), constructors, singletonObjectMap, classReplacements);
    }

    public static Optional<String> getPackageName(String appClassQualifiedName) {
        return ClassUtils.getPackageName(appClassQualifiedName);
    }

    public static String getSimpleName(String appClassQualifiedName) {
        return ClassUtils.getSimpleName(appClassQualifiedName) + "ModuleFactory";
    }

    @UsedInGeneratedCode
    public void addInitMethod(ClassReference owner, String name) {
        getSingleton(owner).addInitMethod(new InitMethod(name));
    }

    @UsedInGeneratedCode
    public void addBeanMethod(ClassReference owner, String name, ClassReference returnType, ParameterReference... parameterReferences) {
        SingletonObject singletonObject = getSingleton(owner);
        getSingleton(owner).addBeanMethod(new BeanMethod(singletonObject, name, returnType, Arrays.asList(parameterReferences)));
    }

    @UsedInGeneratedCode
    public void addConfigField(ClassReference owner, String name, Class<?> fieldType, String key, String defaultValue, boolean mandatory) {
        getSingleton(owner).addConfigField(new ConfigField(owner, name, fieldType, key, defaultValue, mandatory));
    }

    @UsedInGeneratedCode
    public void addDependencyField(ClassReference owner, String name, ClassReference fieldType) {
        getSingleton(owner).addSimpleDependencyField(new SimpleDependencyField(name, fieldType));
    }

    @UsedInGeneratedCode // TODO support arrays in new class ArrayDependencyField
    public void addCollectionDependencyField(ClassReference owner, String name, ClassReference fieldType) {
        getSingleton(owner).addCollectionDependencyField(new CollectionDependencyField(name, owner, fieldType));
    }

    @UsedInGeneratedCode
    public void addConstructor(ClassReference owner, ParameterReference... parameterReferences) {
        getSingleton(owner);// Important !
        constructors.add(new SingletonConstructor(owner, Arrays.asList(parameterReferences)));
    }

    @UsedInGeneratedCode
    public void addClassReplacement(ClassReference type, ClassReference replacement) {
        classReplacements.put(type, replacement);
    }

    public static String getQualifiedName(String appClassQualifiedName) {
        return getPackageName(appClassQualifiedName).map(name -> name + ".").orElse("") + getSimpleName(appClassQualifiedName);
    }

    private SingletonObject getSingleton(ClassReference type) {
        return singletonObjectMap.computeIfAbsent(type, SingletonObject::new);
    }

}
