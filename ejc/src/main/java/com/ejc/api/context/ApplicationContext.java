package com.ejc.api.context;

import lombok.Getter;

import java.util.Set;

public abstract class ApplicationContext {

    public static final String APPLICATION_CLASS_HOLDER_NAME = "com.ejc.ApplicationClassHolderImpl";

    @Getter
    static ApplicationContext instance;

    public abstract <T> T getBean(Class<T> c);

    public abstract <T> T getBean(String c);

    public abstract <T> Set<T> getBeans(Class<T> c);

    public abstract Set<Object> getBeans();

    public abstract <T> Set<T> getBeans(String c);
}
