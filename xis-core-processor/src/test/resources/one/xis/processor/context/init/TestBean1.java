package one.xis.processor.context.init;

import one.xis.Init;
import one.xis.Inject;
import one.xis.Singleton;

@Singleton
public class TestBean1 {

    @Inject
    private TestBean2 bean2;
    private int test = 1;

    @Init
    void init() {
        test += bean2.getTestValue100();
    }


}