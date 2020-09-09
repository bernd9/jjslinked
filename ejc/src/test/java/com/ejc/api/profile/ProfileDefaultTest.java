package com.ejc.api.profile;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileDefaultTest {

    @Test
    void getCurrentProfile() {
        assertThat(Profile.getCurrentProfile()).isEqualTo("default");
    }
}