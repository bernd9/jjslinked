package one.xis.test.testapp;

import one.xis.Inject;
import one.xis.Singleton;
import one.xis.Value;
import lombok.Getter;

import java.util.Set;

@Getter
@Singleton
public class Singleton2 {
    @Value("port")
    private int port;

    @Value("host")
    private String host;

    @Inject
    private Singleton1 singleton1;

    @Inject
    private Singleton4 singleton4;

    @Inject
    private Singleton5 singleton5;

    @Inject
    private Singleton6 singleton6;

    @Inject
    private Set<Interface1> collection;

}
