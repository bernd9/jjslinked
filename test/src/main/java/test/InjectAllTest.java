package test;

import com.ejc.InjectAll;
import com.ejc.Singleton;

import java.util.Set;

@Singleton
public class InjectAllTest {

    @InjectAll
    private Set<CalculatorService> calculatorServices;
}
