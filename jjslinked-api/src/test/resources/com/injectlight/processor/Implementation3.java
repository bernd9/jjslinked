package com.injectlight.processor;

import com.injectlight.Inject;
import com.injectlight.Singleton;

@Singleton
class Implementation3 implements Interf {

    @Inject
    private Test1 testValue;
}