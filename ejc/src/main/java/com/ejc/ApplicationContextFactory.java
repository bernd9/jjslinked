package com.ejc;

public interface ApplicationContextFactory {

    String IMPLEMENTATION_SIMPLE_NAME = "ApplicationContextFactoryImpl";

    ApplicationContext createContext();

    void append(ApplicationContextFactory factory);
}
