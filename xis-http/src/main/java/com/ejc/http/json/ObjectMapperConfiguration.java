package com.ejc.http.json;

import com.ejc.Bean;
import com.ejc.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
class ObjectMapperConfiguration {

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
