package com.ejc.api.context;

import java.util.List;

class BeanMethodSingletonProvider extends SingletonProvider {

    public BeanMethodSingletonProvider(ClassReference type, List<ClassReference> parameterTypes, ApplicationContextInitializer initializer) {
        super(type, parameterTypes, initializer);
    }

    @Override
    Object create() {
        return null;
    }
}
