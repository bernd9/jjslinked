package com.ejc.context2;

import com.ejc.api.context.Module;
import com.ejc.api.context.SimpleDependencyField;
import com.ejc.api.context.SingletonConstructor;
import com.ejc.api.context.*;
import com.ejc.processor.ModuleWriter;
import com.ejc.util.ClassUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public abstract class ModuleFactory {

    @Getter
    private Module module = new Module();

    @Setter
    private SingletonCreationContext context;
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
        getSingleton(owner).addInitMethod(new InitMethod(owner, name));
        //module.getInitInvokers().computeIfAbsent(owner, type -> new HashSet<>()).add(new InitMethodInvoker(owner, name));
    }

    @UsedInGeneratedCode(ModuleWriter.class)
    public void addBeanMethod(ClassReference owner, String name, ClassReference returnType, ClassReference... parameterTypes) {
        getSingleton(owner).addBeanMethod(new BeanMethod(owner, name, returnType, Arrays.asList(parameterTypes)));
    }

    @UsedInGeneratedCode(ModuleWriter.class)
    public void addConfigField(ClassReference owner, String name, Class<?> fieldType, String key, String defaultValue, boolean mandatory) {
        module.getConfigFields().computeIfAbsent(owner, type -> new HashSet<>()).add(new ConfigValueField(owner, name, fieldType, key, defaultValue));
    }

    @UsedInGeneratedCode(ModuleWriter.class)
    public void addDependencyField(ClassReference owner, String name, ClassReference fieldType) {
        module.getDependencyFields().add(new SimpleDependencyField(name, owner, fieldType));
    }

    @UsedInGeneratedCode(ModuleWriter.class) // TODO support arrays in new class ArrayDependencyField
    public void addCollectionDependencyField(ClassReference owner, String name, ClassReference fieldType) {
        getSingleton(owner).addCollectionDependencyField(new CollectionDependencyField(name, owner, fieldType));
    }

    @UsedInGeneratedCode(ModuleWriter.class)
    public void addConstructor(ClassReference owner, ClassReference... parameterTypes) {
        module.getSingletonConstructors().add(new SingletonConstructor(owner, Arrays.asList(parameterTypes)));
    }

    @UsedInGeneratedCode(ModuleWriter.class)
    public void addClassToReplace(ClassReference type) {
        classesToReplace.add(type);
    }

    public static String getQualifiedName(String appClassQualifiedName) {
        return getPackageName(appClassQualifiedName).map(name -> name + ".").orElse("") + getSimpleName(appClassQualifiedName);
    }

    private SingletonObject getSingleton(ClassReference type) {
        SingletonProviders singletonProviders = context.getSingletonProviders();
        SingletonEvents singletonEvents = context.getSingletonEvents();
        return singletonObjectMap.computeIfAbsent(type, t -> new SingletonObject(type, singletonEvents, singletonProviders));
    }

}
