package com.injectlight.processor;

import com.injectlight.Inject;
import com.injectlight.Singleton;

@Singleton
class Implementation1 implements Interf {

    @Inject
    private Test1 testValue;
}