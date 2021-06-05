package one.xis.processor.context.single;

import com.ejc.Inject;
import com.ejc.Singleton;

@Singleton
public class TestBean1 {

    @Inject
    private TestBean2 testBean2;

    @Inject
    private TestBean3 testBean3;

}