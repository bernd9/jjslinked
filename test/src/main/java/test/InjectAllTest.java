package test;

import com.injectlight.InjectAll;
import com.injectlight.Singleton;

import java.util.Set;

@Singleton
public class InjectAllTest {

    @InjectAll
    private Set<CalculatorService> calculatorServices;
}
