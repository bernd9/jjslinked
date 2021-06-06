package one.xis.config;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@Disabled
class ConfigTest {

    @Test
    void values() {
        assertThat(Config.getInstance().getProperty("key1.key2", String.class, "", true)).isEqualTo("xx");
    }

    @Test
    void exceptionIfNotPresentAndMandatory() {
        assertThatThrownBy(() -> Config.getInstance().getProperty("blabla", String.class, "", true)).isInstanceOf(PropertyNotFoundException.class);
    }

    @Test
    void noExceptionIfNotPresentAndNotMandatory() {
        assertThat(Config.getInstance().getProperty("blabla", String.class, "", false)).isNull();
    }

}