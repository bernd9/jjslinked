package com.ejaf;

public interface ApplicationContext {

    <T> T getBean(Class<T> type);
}
