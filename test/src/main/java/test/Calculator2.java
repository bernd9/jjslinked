package test;


import com.ejc.Init;
import com.ejc.Singleton;
import com.ejc.Value;

import java.util.List;

@Singleton
public class Calculator2 extends CalculatorService {

    @Value("ips")
    private List<String> ips;

    @Init
    void init() {
        System.out.println("Huhu !");
    }

    @Init
    void init123() {
        //System.out.println("Huhu !");
    }
}
