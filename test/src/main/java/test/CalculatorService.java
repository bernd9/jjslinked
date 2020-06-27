package test;

import com.injectlight.Inject;
import com.injectlight.Singleton;
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
}
