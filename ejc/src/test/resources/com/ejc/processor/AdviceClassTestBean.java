package com.ejc.processor;

import com.ejc.Singleton;

@Singleton
class AdviceClassTestBean {

    @TestAnnotation
    void testInit(int i, String s) {

    }
}