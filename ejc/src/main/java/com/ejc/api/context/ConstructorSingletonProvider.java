package com.ejc.api.context;

import java.util.List;

class ConstructorSingletonProvider extends SingletonProvider {


    public ConstructorSingletonProvider(ClassReference type, List<ClassReference> parameterTypes, ApplicationContextInitializer initializer) {
        super(type, parameterTypes, initializer);
    }

    @Override
    Object create() {
        return null;
    }
}
