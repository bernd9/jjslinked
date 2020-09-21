package com.ejc.api.context;

class SingleValueInjector extends InjectorBase {

    public SingleValueInjector(ClassReference declaringClass, String fieldName, ClassReference fieldType) {
        super(declaringClass, fieldName, fieldType);
    }

    @Override
    Object getFieldValue(Class<?> fieldType, ApplicationContextFactoryBase factory) {
        return factory.getBean(fieldType);
    }
}
