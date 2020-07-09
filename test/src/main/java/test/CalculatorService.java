package test;

import com.ejc.Init;
import com.ejc.Inject;
import com.ejc.Singleton;
import com.jjslinked.receiver.Receiver;

import javax.validation.constraints.NotNull;

@Singleton
public class CalculatorService {

    @Inject
    private TestBean123 testBean123;

    @Receiver
    int square(@NotNull Integer i) {
        return i*i;
    }

    @Init
    void init() {
        System.out.println("Huhu 2 !");
    }
}
