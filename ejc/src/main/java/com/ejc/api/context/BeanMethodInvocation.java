package com.ejc.api.context;

import lombok.RequiredArgsConstructor;

class BeanMethodInvocation {
    private enum Requirement {
        CREATED,
        DEPENDENCIES,
        INITIALIZED
    }

    void addBeanMethod() {

    }


    @RequiredArgsConstructor
    private class BemMethodWrapper {
        private final BeanMethod beanMethod;
    }

}
