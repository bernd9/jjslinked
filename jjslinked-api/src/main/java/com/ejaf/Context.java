package com.ejaf;

public interface Context {

    <T> T getBean(Class<T> type);
}
