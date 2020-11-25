package com.ejc.processor;

import com.ejc.Singleton;

import java.util.Set;

@Singleton
class InjectAll {

    @com.ejc.InjectAll
    private Set<Interf> implementations;
}