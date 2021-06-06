package one.xis.processor.context.single;

import one.xis.Inject;
import one.xis.Singleton;

@Singleton
public class TestBean1 {

    @Inject
    private TestBean2 testBean2;

    @Inject
    private TestBean3 testBean3;

}