package com.ejc;

import com.jjslinked.ApplicationContext;

public interface ComponentProvider {

    <T> T getComponent(Class<T> type, ApplicationContext context);

}
