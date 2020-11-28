package test;

//import com.ejc.http.RestController;

//@RestController
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

    //@Get
    Greeting greeting() {
        return new Greeting("Huhu !");
    }
}
