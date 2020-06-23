package com.injectlight.processor;

import com.injectlight.Inject;
import com.injectlight.Singleton;

@Singleton
class Test2 {

    @Inject
    private Test1 test;
}