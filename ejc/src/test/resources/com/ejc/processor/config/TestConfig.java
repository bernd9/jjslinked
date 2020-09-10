package com.ejc.processor.context.config;

import com.ejc.Bean;
import com.ejc.Configuration;
import com.ejc.Init;

@Configuration
public class TestConfig {

    private int i = 1;

    @Init
    void init() {
        i++;
    }

    @Bean
    TestBean2 getTest2() {
        return new TestBean2(i);
    }

}