package com.jjslinked.spring;

import com.jjslinked.DefaultParameterProviderRegistry;
import com.jjslinked.ParameterProviderRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SpringContextConfiguration {

    @Bean
    ParameterProviderRegistry parameterProviderRegistry() {
        return new DefaultParameterProviderRegistry();
    }


}
