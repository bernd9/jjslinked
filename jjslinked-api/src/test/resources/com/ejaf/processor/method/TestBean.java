package com.ejaf.processor;

public abstract class TestBean {

    @TestInvokerAnnotation
    public void test(@TestParamAnnnotation String xyz) {

    }
}
