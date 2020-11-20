package com.ejc.test.testapp;

import com.ejc.Inject;
import com.ejc.Singleton;
import com.ejc.Value;
import lombok.Getter;

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

}
