package com.ejc.processor;

import com.ejc.Inject;
import com.ejc.Singleton;

@Singleton
class Test2 {

    @Inject
    private Test1 testValue;
}