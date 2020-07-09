package com.ejc;

import java.util.Set;

public interface ApplicationContext {
    void addBean(Object bean);

    <T> T getBean(Class<T> c);

    <T> Set<T> getBeans(Class<T> c);
}
