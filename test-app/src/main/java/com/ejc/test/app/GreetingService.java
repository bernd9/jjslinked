package com.ejc.test.app;

import com.ejc.Singleton;

@Singleton
class GreetingService {

    Greeting getGreeting() {
        return new Greeting("Hello !");
    }
}
