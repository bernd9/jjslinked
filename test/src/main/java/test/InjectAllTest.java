package test;

import com.ejc.Inject;
import com.ejc.Singleton;

import java.util.Set;

@Singleton
public class InjectAllTest {

    @Inject
    private Set<CalculatorService> calculatorServices;

    @Inject
    private Set<Object> objects;
}
