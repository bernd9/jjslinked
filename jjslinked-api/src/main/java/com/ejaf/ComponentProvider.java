package com.ejaf;

public interface ComponentProvider {

    <T> T getComponent(Class<T> type, Context context);

}
