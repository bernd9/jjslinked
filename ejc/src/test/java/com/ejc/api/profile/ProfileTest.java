package com.ejc.api.profile;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
class ProfileTest {

    // TODO
    @Test
    void getCurrentProfile() {
        System.setProperty("ejc-profile", "test");
        assertThat(Profile.getCurrentProfile()).isEqualTo("test");
    }

}