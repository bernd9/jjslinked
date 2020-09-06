package com.ejc.processor.context.init;

import com.ejc.Init;
import com.ejc.Inject;
import com.ejc.Singleton;

@Singleton
public class TestBean1 {

    @Inject
    private TestBean2 bean2;
    private int test = 1;

    @Init
    void init() {
        test += bean2.getTestValue100();
    }


}