package com.ejc.processor;

import com.ejc.Inject;
import com.ejc.Singleton;

@Singleton
class Implementation2 implements Interf {

    @Inject
    private Test1 testValue;
}