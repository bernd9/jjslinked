package com.ejc.api.context;

import com.ejc.context2.ClassReference;
import com.ejc.context2.UsedInGeneratedCode;
import com.ejc.processor.ModuleWriter;
import com.ejc.util.ClassUtils;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

public class ModuleFactory {

    @Getter
    private Module module = new Module();

    public static Optional<String> getPackageName(String appClassQualifiedName) {
        return ClassUtils.getPackageName(appClassQualifiedName);
    }

    public static String getSimpleName(String appClassQualifiedName) {
        return ClassUtils.getSimpleName(appClassQualifiedName) + "ModuleFactory";
    }

    @UsedInGeneratedCode(ModuleWriter.class)
    public void addInitMethod(ClassReference owner, String name) {
        module.getInitInvokers().computeIfAbsent(owner, type -> new HashSet<>()).add(new InitMethodInvoker(owner, name));
    }

    @UsedInGeneratedCode(ModuleWriter.class)
    public void addBeanMethod(ClassReference owner, String name, ClassReference returnType, ClassReference... parameterTypes) {
        module.getBeanMethods().computeIfAbsent(owner, type -> new HashSet<>())
                .add(new BeanMethod(owner, name, returnType, Arrays.asList(parameterTypes)));
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
        module.getCollectionDependencyFields().computeIfAbsent(owner, type -> new HashSet<>()).add(new CollectionDependencyField(name, owner, fieldType));
    }

    @UsedInGeneratedCode(ModuleWriter.class)
    public void addConstructor(ClassReference owner, ClassReference... parameterTypes) {
        module.getSingletonConstructors().add(new SingletonConstructor(owner, Arrays.asList(parameterTypes)));
    }

    @UsedInGeneratedCode(ModuleWriter.class)
    public void addClassToReplace(ClassReference type) {
        module.getClassesToReplace().add(type);
    }

    public static String getQualifiedName(String appClassQualifiedName) {
        return getPackageName(appClassQualifiedName).map(name -> name + ".").orElse("") + getSimpleName(appClassQualifiedName);
    }


}
