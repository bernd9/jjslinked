package one.xis.test;

import one.xis.Inject;
import one.xis.test.testapp.Singleton1;
import one.xis.test.testapp.Singleton2;
import one.xis.test.testapp.Singleton3;
import one.xis.test.testapp.Singleton6;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.util.MockUtil;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(IntegrationTestExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntegrationtestTest {

    @Mock
    private Singleton1 singleton1;

    @Spy
    private Singleton6 singleton6;

    @Inject
    private Singleton2 singleton2;

    @Inject
    private Singleton3 singleton3;

    @Test
    void fieldInjectionOk() {
        assertThat(singleton2.getSingleton1()).isNotNull();
        assertThat(singleton2.getSingleton4()).isNotNull();
        assertThat(singleton2.getSingleton5()).isNotNull();
        assertThat(singleton2.getSingleton6()).isNotNull();
        assertThat(singleton2.getCollection().size()).isEqualTo(4);
    }

    @Test
    void parameterInjectionOk() {
        assertThat(singleton3.getSingleton1()).isNotNull();
        assertThat(singleton3.getSingleton4()).isNotNull();
        assertThat(singleton3.getSingleton5()).isNotNull();
        assertThat(singleton3.getSingleton6()).isNotNull();
        assertThat(singleton3.getCollection().size()).isEqualTo(4);
    }

    @Test
    void singletonReplacedByMock() {
        assertThat(MockUtil.isMock(singleton2.getSingleton1())).isTrue();
        assertThat(MockUtil.isMock(singleton3.getSingleton1())).isTrue();
    }

    @Test
    void singletonReplacedBySpy() {
        assertThat(MockUtil.isSpy(singleton2.getSingleton6())).isTrue();
        assertThat(MockUtil.isSpy(singleton3.getSingleton6())).isTrue();
    }
}
