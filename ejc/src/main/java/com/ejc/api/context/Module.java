package com.ejc.api.context;

import com.ejc.api.context.model.ConfigValueField;
import lombok.Getter;

import java.util.*;

@Getter
public class Module {
    private Map<ClassReference, SingletonConstructor> singletonConstructors = new HashMap<>();
    private Map<ClassReference, Collection<BeanMethod>> beanMethods = new HashMap<>();
    private Map<ClassReference, Collection<InitMethodInvoker>> initInvokers = new HashMap<>();
    private Map<ClassReference, Collection<DependencyField>> dependencyFields = new HashMap<>();
    private Map<ClassReference, Collection<ConfigValueField>> configFields = new HashMap<>();
    private Set<ClassReference> classesToReplace = new HashSet<>();
}
