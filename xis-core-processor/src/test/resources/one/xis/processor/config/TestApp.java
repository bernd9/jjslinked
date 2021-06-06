package one.xis.processor.config;

import one.xis.Application;
import one.xis.Singleton;
import one.xis.Value;

import java.util.Map;
import java.util.Set;

@Application
class TestApp {

}

@Singleton
class Singleton1 {

    @Value("simpleString")
    private String simpleString;

    @Value("simpleInt")
    private int simpleInt;

    @Value("integerCollection")
    private Set<Integer> integerCollection;

    @Value("map")
    private Map<String, Integer> map;

}

@Singleton
class Singleton2 {

    private String simpleString;
    private int simpleInt;
    private Set<Integer> integerCollection;
    private Map<String, Integer> map;

    Singleton2(@Value("simpleString") String simpleString,
               @Value("simpleInt") int simpleInt,
               @Value("integerCollection") Set<Integer> integerCollection,
               @Value("map") Map<String, Integer> map) {

        this.simpleString = simpleString;
        this.simpleInt = simpleInt;
        this.integerCollection = integerCollection;
        this.map = map;
    }
}