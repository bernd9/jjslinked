package com.ejc.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CollectorUtilsTest {

    @Test
    void toOnlyElementEmpty() {
        List<Integer> list = new ArrayList<>();
        assertThatThrownBy(() -> list.stream().collect(CollectorUtils.toOnlyElement())).hasMessage("stream is empty");
    }

    @Test
    void toOnlyElementOneElement() {
        List<Integer> list = List.of(1);
        assertThat(list.stream().collect(CollectorUtils.toOnlyElement())).isEqualTo(1);
    }

    @Test
    void toOnlyElementOneMany() {
        List<Integer> list = List.of(1, 2);
        assertThatThrownBy(() -> list.stream().collect(CollectorUtils.toOnlyElement())).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void toOnlyOptionalEmpty() {
        List<Integer> list = new ArrayList<>();
        assertThat(list.stream().collect(CollectorUtils.toOnlyOptional())).isEmpty();
    }

    @Test
    void toOnlyOptionalPresent() {
        List<Integer> list = List.of(1);
        assertThat(list.stream().collect(CollectorUtils.toOnlyOptional())).isPresent();
    }

    @Test
    void toOnlyOptionalTooMany() {
        List<Integer> list = List.of(1, 2);
        assertThatThrownBy(() -> list.stream().collect(CollectorUtils.toOnlyOptional())).isInstanceOf(IllegalStateException.class);
    }
}