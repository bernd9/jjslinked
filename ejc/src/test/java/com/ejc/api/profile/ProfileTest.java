package com.ejc.api.profile;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileTest {

    @Test
    void getCurrentProfile() {
        System.setProperty("profile", "test");
        assertThat(Profile.getCurrentProfile()).isEqualTo("test");
    }

}