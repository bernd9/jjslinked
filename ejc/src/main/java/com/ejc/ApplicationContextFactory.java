package com.ejc;

import java.util.Collection;
import java.util.Set;

public interface ApplicationContextFactory {

    String IMPLEMENTATION_SIMPLE_NAME = "ApplicationContextFactoryImpl";

    ApplicationContext createContext();

    void removeBeanClasses(Collection<Class<?>> classes);

    Set<Class<?>> getClassesToReplace();

    void append(ApplicationContextFactory factory);
}
