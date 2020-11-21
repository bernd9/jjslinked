package test;

//import com.ejc.http.Get;

public class CalculationController {


    class Greeting {
        public String getValue() {
            return value;
        }

        public Greeting(String value) {
            this.value = value;
        }

        private final String value;
    }

    Greeting greeting() {
        return new Greeting("Huhu !");
    }
}
