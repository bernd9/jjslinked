package com.ejc.processor;

import com.ejc.Singleton;

@Singleton
class AdviceTestBean {

    @TestAnnotation
    void testInit(int i, String s) {

    }

    int xyz(String s) {
        return (int) s.length();
    }
}