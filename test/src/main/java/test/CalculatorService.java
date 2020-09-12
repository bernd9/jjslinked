package test;

import com.ejc.Init;
import com.ejc.Inject;
import com.ejc.Singleton;


@Singleton
public class CalculatorService {

    @Inject
    private TestApp testApp;

    int square(Integer i) {
        return i*i;
    }

    @Init
    void init() {
       // System.out.println("Huhu 2 !");
    }
}
