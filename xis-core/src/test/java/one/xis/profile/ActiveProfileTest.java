package one.xis.profile;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
class ActiveProfileTest {

    // TODO
    @Test
    void getCurrentProfile() {
        System.setProperty("ejc-profile", "test");
        assertThat(ActiveProfile.getCurrentProfile()).isEqualTo("test");
    }

}