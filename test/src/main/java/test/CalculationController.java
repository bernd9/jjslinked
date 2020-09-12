package test;

import com.ejc.http.Get;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class CalculationController {

    @Getter
    @RequiredArgsConstructor
    class Greeting {
        private final String value;
    }

    @Get("/greeting")
    Greeting greeting() {
        return new Greeting("Huhu !");
    }
}
