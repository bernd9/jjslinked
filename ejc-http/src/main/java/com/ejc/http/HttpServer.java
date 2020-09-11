package com.ejc.http;

import com.ejc.Application;
import com.ejc.ApplicationRunner;

@Application
public class HttpServer {

    public static void main(String[] s) {
        ApplicationRunner.run(HttpServer.class);
    }

}
