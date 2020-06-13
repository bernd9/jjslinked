package com.jjslinked;

public interface ApplicationContext {

    <T> T getBean(Class<T> type);
}
