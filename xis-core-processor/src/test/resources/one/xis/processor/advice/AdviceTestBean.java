package one.xis.processor.advice;

import one.xis.Singleton;

@Singleton
class AdviceTestBean {

    @TestAnnotation
    void test1(int i, String s) {

    }

    @TestAnnotation
    void test2() {

    }

    @TestAnnotation
    String test3() {
        return "Huhu !";
    }


    @TestAnnotation
    int test4(String s) {
        return s.length();
    }


    int xyz(String s) {
        return (int) s.length();
    }
}