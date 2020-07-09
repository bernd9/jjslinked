package com.ejc;

import com.ejc.processor.ApplicationContextFactoryBase;
import com.ejc.processor.BeanUtils;

import java.util.Set;

public abstract class ApplicationContext {

    private static ApplicationContext instance;

    public abstract <T> T getBean(Class<T> c);

    public abstract <T> T getBean(String c);

    public abstract <T> Set<T> getBeans(Class<T> c);

    public static synchronized ApplicationContext getInstance() {
        if (instance == null) {
            ApplicationContextFactoryBase factory = (ApplicationContextFactoryBase) BeanUtils.createInstance("com.ejc.generated.ApplicationContextFactory");
            instance = factory.createContext();
        }
        return instance;

    }
}
