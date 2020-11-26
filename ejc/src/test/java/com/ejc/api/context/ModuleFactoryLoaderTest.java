package com.ejc.api.context;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModuleFactoryLoaderTest {

    @Test
    void load2() {
        ModuleFactoryLoader loader = new ModuleFactoryLoader();
        assertThat(loader.load()).containsExactly(new TestModuleFactory());
    }
}