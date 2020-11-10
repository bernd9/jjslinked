package test;

import com.ejc.Inject;
import com.ejc.Singleton;
import com.ejc.SystemProperty;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class TestBean1 {

    void test() {

    }


    private final TestBean2 testBean2;

    private final TestBean3 testBean3;

    private final TestBean4 testBean4;

    @Inject
    private TestBean1 testBean1;

    @Inject
    private TestBean5 testBean5;

    @Inject
    private TestBean6 testBean6;

    @Inject
    private TestBean7 testBean7;

    @Inject
    private TestBean8 testBean8;

    @Inject
    private TestBean9 testBean9;

    @Inject
    private TestBean10 testBean10;

    @SystemProperty(defaultValue = "3306", name = "port")
    private int port;
}
