package test;


import com.ejc.Init;
import com.ejc.Singleton;

@Singleton
public class Calculator2 extends CalculatorService{

    @Init
    void init() {
        System.out.println("Huhu !");
    }

    @Init
    void init123() {
        System.out.println("Huhu !");
    }
}
