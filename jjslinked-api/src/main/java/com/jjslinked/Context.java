package com.jjslinked;

public interface Context {

    public <T> T getBean(Class<T> clazz);
}
