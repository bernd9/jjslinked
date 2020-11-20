package com.ejc.test.testapp;

import com.ejc.Singleton;
import com.ejc.Value;
import lombok.Getter;

@Singleton
@Getter
public class Singleton3 {

    private final String host;
    private final int port;
    private final Singleton1 singleton1;
    private final Singleton4 singleton4;
    private final Singleton5 singleton5;
    private final Singleton6 singleton6;

    Singleton3(Singleton1 singleton1,
               @Value("host") String host,
               @Value("port") int port,
               Singleton4 singleton4,
               Singleton5 singleton5,
               Singleton6 singleton6) {
        this.singleton1 = singleton1;
        this.singleton4 = singleton4;
        this.singleton5 = singleton5;
        this.singleton6 = singleton6;
        this.host = host;
        this.port = port;
    }
}
