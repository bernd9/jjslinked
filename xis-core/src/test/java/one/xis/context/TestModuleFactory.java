package one.xis.context;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class TestModuleFactory extends ModuleFactory {
    public TestModuleFactory() {
        super(ClassReference.getRef("one.xis.context.TestModuleFactory"));
    }
}
