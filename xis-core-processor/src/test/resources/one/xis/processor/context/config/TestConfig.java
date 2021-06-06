package one.xis.processor.context.config;

import one.xis.Bean;
import one.xis.Configuration;
import one.xis.Init;

@Configuration
public class TestConfig {
    private int i = 1;

    @Init
    void init() {
        i++;
    }

    @Bean
    TestBean getTest() {
        return new TestBean(i);
    }

}