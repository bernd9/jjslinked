package com.ejc.processor.context.multi;

import com.ejc.InjectAll;
import com.ejc.Singleton;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Singleton
public class TestBean1 {

    @InjectAll
    private Collection<TestInterface> test1;

    @InjectAll
    private Set<TestInterface> test2;

    @InjectAll
    private List<TestInterface> test3;

    @InjectAll
    private LinkedList<TestInterface> test4;

    /*
    @InjectAll
    private TestInterface[] test5;
    */


}