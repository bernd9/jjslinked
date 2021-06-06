package one.xis.processor;

import one.xis.Singleton;

import java.util.Set;

@Singleton
class InjectAll {

    @com.ejc.InjectAll
    private Set<Interf> implementations;
}