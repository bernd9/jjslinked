package one.xis.processor.context.config;

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
    TestBean getTest() {
        return new TestBean(i);
    }

}