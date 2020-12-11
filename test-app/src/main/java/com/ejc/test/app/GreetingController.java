package com.ejc.test.app;

import com.ejc.Inject;
import com.ejc.http.Get;
import com.ejc.http.RestController;

@RestController
class GreetingController {

    @Inject
    private GreetingService greetingService;

    @Get
    Greeting getGreeting() {
        return greetingService.getGreeting();
    }
}
